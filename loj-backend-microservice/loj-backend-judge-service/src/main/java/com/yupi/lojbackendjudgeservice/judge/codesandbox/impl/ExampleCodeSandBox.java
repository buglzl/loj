package com.yupi.lojbackendjudgeservice.judge.codesandbox.impl;


import com.yupi.lojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.yupi.lojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.yupi.lojbackendmodel.codesandbox.ExecuteCodeResponse;
import com.yupi.lojbackendmodel.codesandbox.JudgeInfo;
import com.yupi.lojbackendmodel.enums.JudgeInfoEnum;
import com.yupi.lojbackendmodel.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * 实例代码沙箱
 */
public class ExampleCodeSandBox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> inputList = executeCodeRequest.getInputList();
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoEnum.WRONG_ANSWER.getValue());
        judgeInfo.setTime(1000L);
        judgeInfo.setMemory(1000L);

        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());

        System.out.println("实例代码沙箱");
        return executeCodeResponse;
    }
}
