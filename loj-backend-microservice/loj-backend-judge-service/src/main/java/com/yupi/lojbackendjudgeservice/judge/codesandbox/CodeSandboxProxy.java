package com.yupi.lojbackendjudgeservice.judge.codesandbox;

import com.yupi.lojbackendmodel.codesandbox.ExecuteCodeRequest;
import com.yupi.lojbackendmodel.codesandbox.ExecuteCodeResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@Slf4j
public class CodeSandboxProxy implements CodeSandbox{

    private final CodeSandbox codeSandbox;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱请求信息：" + executeCodeRequest.toString());
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        log.info("代码沙箱返回信息：" + executeCodeResponse.toString());
        return executeCodeResponse;
    }
}
