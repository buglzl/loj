package com.yupi.lojbackendjudgeservice.judge.codesandbox.impl;


import com.yupi.lojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.yupi.lojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.yupi.lojbackendmodel.codesandbox.ExecuteCodeResponse;

/**
 * 第三方代码沙箱
 */
public class ThridPartyCodeSandBox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
