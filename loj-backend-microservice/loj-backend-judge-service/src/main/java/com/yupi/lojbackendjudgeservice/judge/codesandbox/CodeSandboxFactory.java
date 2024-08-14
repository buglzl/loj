package com.yupi.lojbackendjudgeservice.judge.codesandbox;

import com.yupi.lojbackendjudgeservice.judge.codesandbox.impl.ExampleCodeSandBox;
import com.yupi.lojbackendjudgeservice.judge.codesandbox.impl.RemoteCodeSandBox;
import com.yupi.lojbackendjudgeservice.judge.codesandbox.impl.ThridPartyCodeSandBox;

/**
 * 沙箱工厂模式
 */
public class CodeSandboxFactory {
    /**
     * 根据给定的参数，创建相应的代码沙箱
     * @param type
     * @return
     */
    public static CodeSandbox newInstance(String type) {
        switch (type) {
            case "example": return new ExampleCodeSandBox();
            case "remote" : return new RemoteCodeSandBox();
            case "thirdParty": return new ThridPartyCodeSandBox();
            default: return new ExampleCodeSandBox();
        }
    }
}
