package com.yupi.loj.judge.codesandbox;

import com.yupi.loj.judge.codesandbox.model.ExecuteCodeRequest;
import com.yupi.loj.judge.codesandbox.model.ExecuteCodeResponse;


public interface CodeSandbox {
    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
