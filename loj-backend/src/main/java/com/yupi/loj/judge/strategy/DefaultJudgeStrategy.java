package com.yupi.loj.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.yupi.loj.model.dto.question.JudgeCase;
import com.yupi.loj.model.dto.question.JudgeConfig;
import com.yupi.loj.judge.codesandbox.model.JudgeInfo;
import com.yupi.loj.model.entity.Question;
import com.yupi.loj.model.enums.JudgeInfoEnum;

import java.util.List;

public class DefaultJudgeStrategy implements JudgeStrategy{
    /**
     * 默认判题策略
     * @param judgeContext
     * @return
     */
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {

        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Question question = judgeContext.getQuestion();

        Long acturalTime = judgeInfo.getTime();
        Long acturalMemory = judgeInfo.getMemory();

        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setTime(acturalTime);
        judgeInfoResponse.setMemory(acturalMemory);

        JudgeInfoEnum judgeInfoEnum = JudgeInfoEnum.WAITING;

        // 判断题目输出数量是否一致
        if (outputList.size() != inputList.size()) {
            judgeInfoEnum = JudgeInfoEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoEnum.getValue());
            return judgeInfoResponse;
        }
        // todo 这个逻辑需要修改
        // 判断题目每个case输出结果是否一致
        for (int i = 0; i < judgeCaseList.size(); i ++ ) {
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (!judgeCase.getOutput().equals(outputList.get(i))) {
                judgeInfoEnum = JudgeInfoEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoEnum.getValue());
                return judgeInfoResponse;
            }
        }
        // 判断题目时间限制

        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long needTimeLimit = judgeConfig.getTimeLimit();
        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        if (acturalMemory > needMemoryLimit) {
            judgeInfoEnum = JudgeInfoEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoEnum.getValue());
            return judgeInfoResponse;
        }
        if (acturalTime > needTimeLimit) {
            judgeInfoEnum = JudgeInfoEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoEnum.getValue());
            return judgeInfoResponse;
        }

        judgeInfoEnum = JudgeInfoEnum.ACCEPTED;
        judgeInfoResponse.setMessage(judgeInfoEnum.getValue());
        return judgeInfoResponse;
    }
}
