package com.yupi.loj.judge.codesandbox.impl;

import com.yupi.loj.judge.codesandbox.CodeSandbox;
import com.yupi.loj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.loj.judge.codesandbox.model.ExecuteCodeResponse;

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
