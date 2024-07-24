package com.yupi.lojcodesandbox;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.yupi.lojcodesandbox.model.ExecuteCodeRequest;
import com.yupi.lojcodesandbox.model.ExecuteCodeResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class JavaNativeCodeSandbox implements CodeSandbox{
    private static final String GLOBAL_DIR = "tmpCode";
    private static final String GLOBAL_CLASS = "Main.java";
    private static String rootPathName;
    private static String globalCodePathName;

    static {
        // 由于这段代码每次调用这个函数的时候都会有，因此设置为静态块
        rootPathName = System.getProperty("user.dir");
        globalCodePathName = rootPathName + File.separator + GLOBAL_DIR;
        // 判断该文件夹是否存在 注意这个文件
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }
    }

    public static void main(String[] args) {
        CodeSandbox codeSandbox = new JavaNativeCodeSandbox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        String code = ResourceUtil.readStr("testCode/SimpleComputeArgs/Main.java", StandardCharsets.UTF_8);
        executeCodeRequest.setLanguage("java");
        executeCodeRequest.setCode(code);
        executeCodeRequest.setInputList(Arrays.asList("1 2", "2 3"));
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String language = executeCodeRequest.getLanguage();
        String code = executeCodeRequest.getCode();
        List<String> inputList = executeCodeRequest.getInputList();

        // 把用户的文件放在某个确定的文件夹下
        String userCodePathParentName = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodePathParentName + File.separator + GLOBAL_CLASS;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);

        // 执行命令行
        String compileCmd = String.format("javac -encoding utf-8%s", userCodeFile.getAbsoluteFile());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            int exitValue = compileProcess.waitFor();
            if (exitValue == 0) {
                System.out.println("编译成功");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()));
                StringBuilder compileOutputStringbuilder = new StringBuilder();
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    compileOutputStringbuilder.append(compileOutputLine);
                }
                System.out.println("正常输出: " + compileOutputStringbuilder);
            } else {
                System.out.println("编译失败: " + exitValue);

                // 获取正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(compileProcess.getInputStream()));
                StringBuilder compileOutputStringbuilder = new StringBuilder();
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    compileOutputStringbuilder.append(compileOutputLine);
                }
                System.out.println("正常输出: " + compileOutputStringbuilder);

                // 获取错误输出
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()));
                StringBuilder compileErrorOutputStringbuilder = new StringBuilder();
                String errorCompileOutputLine;
                while ((errorCompileOutputLine = errorBufferedReader.readLine()) != null) {
                    compileErrorOutputStringbuilder.append(errorCompileOutputLine);
                }
                System.out.println("错误输出: " + compileErrorOutputStringbuilder);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
