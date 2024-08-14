package com.yupi.lojbackendjudgeservice.judge.codesandbox;


import com.yupi.lojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.yupi.lojbackendmodel.codesandbox.ExecuteCodeResponse;

public interface CodeSandbox {
    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
