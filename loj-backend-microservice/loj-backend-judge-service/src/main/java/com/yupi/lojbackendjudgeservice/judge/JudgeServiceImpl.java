package com.yupi.lojbackendjudgeservice.judge;

import cn.hutool.json.JSONUtil;
import com.yupi.lojbackendcommon.common.ErrorCode;
import com.yupi.lojbackendcommon.exception.BusinessException;
import com.yupi.lojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.yupi.lojbackendjudgeservice.judge.codesandbox.CodeSandboxFactory;
import com.yupi.lojbackendjudgeservice.judge.codesandbox.CodeSandboxProxy;
import com.yupi.lojbackendjudgeservice.judge.strategy.JudgeContext;
import com.yupi.lojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.yupi.lojbackendmodel.codesandbox.ExecuteCodeResponse;
import com.yupi.lojbackendmodel.codesandbox.JudgeInfo;
import com.yupi.lojbackendmodel.dto.question.JudgeCase;
import com.yupi.lojbackendmodel.entity.Question;
import com.yupi.lojbackendmodel.entity.QuestionSubmit;
import com.yupi.lojbackendmodel.enums.JudgeInfoEnum;
import com.yupi.lojbackendmodel.enums.QuestionSubmitStatusEnum;
import com.yupi.lojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService{
    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;
    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        // 1) 安全做到位，判断提交信息，题目是否存在
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2) 判断该提交状态是否为等待中，是就将其状态设置为运行中
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "正在判题中");
        }
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        // 3) 调用代码沙箱 获取结果
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());

        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .inputList(inputList)
                .language(language)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        System.out.println("获取沙箱返回" + executeCodeResponse);
        // 如果沙箱返回什么也没有，算系统错误，如果编译错误 message 为 compiler error
        // 系统错误

        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);

        if (executeCodeResponse.getStatus() == null) {
            // 系统错误 记录留下，但是不算做提交数
            question = questionFeignClient.getQuestionById(questionId);
            question.setSubmitNum(question.getSubmitNum() - 1);

            JudgeInfo systemErrorJudgeInfo = new JudgeInfo();
            systemErrorJudgeInfo.setMessage(JudgeInfoEnum.SYSTEM_ERROR.getValue());
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(systemErrorJudgeInfo));
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());

            questionFeignClient.updateQuestionById(question);
            questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统内部异常");
        }

        // 编译错误
        if ("compile error".equals(executeCodeResponse.getMessage())) {
            JudgeInfo compileErrorJudgeInfo = new JudgeInfo();
            compileErrorJudgeInfo.setMessage(JudgeInfoEnum.COMPILE_ERROR.getValue());
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(compileErrorJudgeInfo));
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
            update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
            if (!update) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
            }
            return questionFeignClient.getQuestionSubmitById(questionSubmitId);
        }

        // 4) 根据沙箱的执行结果，设置题目的判题状态和信息
        // 这个默认WAITTING表示的是已经执行完代码沙箱，现在在判断题目最终结果
        JudgeInfo judgeInfo = executeCodeResponse.getJudgeInfo();
        List<String> outputList = executeCodeResponse.getOutputList();
        // 5) 根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(judgeInfo);
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);

        JudgeInfo judgeInfoFinal = judgeManager.doJudge(judgeContext);
        // 6) 修改数据库中的结果

        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfoFinal));
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }

        if (JudgeInfoEnum.ACCEPTED.getValue().equals(judgeInfoFinal.getMessage())) {
            question = questionFeignClient.getQuestionById(questionId);
            question.setAcceptedNum(question.getAcceptedNum() + 1);
            questionFeignClient.updateQuestionById(question);
        }

        return questionFeignClient.getQuestionSubmitById(questionSubmitId);
    }
}
