package com.yupi.loj.judge;

import cn.hutool.json.JSONUtil;
import com.yupi.loj.common.ErrorCode;
import com.yupi.loj.exception.BusinessException;
import com.yupi.loj.judge.codesandbox.CodeSandbox;
import com.yupi.loj.judge.codesandbox.CodeSandboxFactory;
import com.yupi.loj.judge.codesandbox.CodeSandboxProxy;
import com.yupi.loj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.loj.judge.codesandbox.model.ExecuteCodeResponse;
import com.yupi.loj.judge.strategy.JudgeContext;
import com.yupi.loj.model.dto.question.JudgeCase;
import com.yupi.loj.judge.codesandbox.model.JudgeInfo;
import com.yupi.loj.model.entity.Question;
import com.yupi.loj.model.entity.QuestionSubmit;
import com.yupi.loj.model.enums.QuestionSubmitStatusEnum;
import com.yupi.loj.service.QuestionService;
import com.yupi.loj.service.QuestionSubmitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService{
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;
    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        // 1) 安全做到位，判断提交信息，题目是否存在
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
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
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
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
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfoFinal));
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        return questionSubmitService.getById(questionSubmitId);
    }
}
