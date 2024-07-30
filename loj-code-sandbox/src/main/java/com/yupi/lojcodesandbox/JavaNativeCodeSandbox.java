package com.yupi.lojcodesandbox;


import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import cn.hutool.poi.excel.ExcelUtil;
import com.yupi.lojcodesandbox.model.ExecuteCodeRequest;
import com.yupi.lojcodesandbox.model.ExecuteCodeResponse;
import com.yupi.lojcodesandbox.model.ExecuteMessage;
import com.yupi.lojcodesandbox.model.JudgeInfo;
import com.yupi.lojcodesandbox.security.DefaultSecurityManager;
import com.yupi.lojcodesandbox.utils.ProcessUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JavaNativeCodeSandbox implements CodeSandbox{
    private static final String GLOBAL_DIR = "tmpCode";
    private static final String GLOBAL_CLASS = "Main.java";
    private static final String rootPathName;
    private static final String globalCodePathName;
    private static final List<String> blackList = Arrays.asList("Files", "exec");
    private static final WordTree WORD_TREE;

    private static final long TIME_OUT = 10000L;

    static {
        // 由于这段代码每次调用这个函数的时候都会有，因此设置为静态块
        rootPathName = System.getProperty("user.dir");
        globalCodePathName = rootPathName + File.separator + GLOBAL_DIR;
        // 判断该文件夹是否存在 注意这个文件
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }
    }

    static {
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(blackList);
    }

    public static void main(String[] args) {
        CodeSandbox codeSandbox = new JavaNativeCodeSandbox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
//        String code = ResourceUtil.readStr("testCode/unsafeCode/RunFileError.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/unsafeCode/WriteFileError.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/unsafeCode/ReadFileError.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/unsafeCode/MemoryError.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/unsafeCode/SleepError.java", StandardCharsets.UTF_8);
        String code = ResourceUtil.readStr("testCode/SimpleComputeArgs/Main.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/SimpleCompute/Main.java", StandardCharsets.UTF_8);
        executeCodeRequest.setLanguage("java");
        executeCodeRequest.setCode(code);
        executeCodeRequest.setInputList(Arrays.asList("1\n3 2 6\n", "1\n1 2 6\n"));
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.setSecurityManager(new DefaultSecurityManager());

        String language = executeCodeRequest.getLanguage();
        String code = executeCodeRequest.getCode();
        List<String> inputList = executeCodeRequest.getInputList();

        FoundWord foundWord = WORD_TREE.matchWord(code);
        if (foundWord != null) {
            System.out.println("包含禁止词: " + foundWord.getFoundWord());
            return null;
        }

        // 把用户的文件放在某个确定的文件夹下
        String userCodePathParentName = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodePathParentName + File.separator + GLOBAL_CLASS;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);

        // 编译代码
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsoluteFile());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeCompileMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            System.out.println(executeCompileMessage);
        } catch (IOException e) {
            return getErrorResponse(e);
        }
        // 注意，如果编译失败就不能往下来，日后还需要改这部分代码
        // 运行代码
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs: inputList) {
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodePathParentName, inputArgs);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                // 利用线程进行超时控制
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        runProcess.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                ExecuteMessage executeRunMessage = ProcessUtils.runProcessAndGetMessage(runProcess, "运行");
                System.out.println(executeRunMessage);
                executeMessageList.add(executeRunMessage);
            } catch (IOException e) {
                return getErrorResponse(e);
            }
        }
        // 获取输出
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();
        Long maxTime = 0L;
        for (ExecuteMessage executeMessage: executeMessageList) {
            String errorMessage = executeMessage.getErrorMessage();
            if (StrUtil.isNotBlank(errorMessage)) {
                executeCodeResponse.setMessage(errorMessage);
                // 执行中存在错误
                executeCodeResponse.setStatus(3);
                break;
            }
            outputList.add(executeMessage.getMessage());
            Long time = executeMessage.getTime();
            if (time != null) {
                maxTime = Math.max(maxTime, time);
            }
        }

        executeCodeResponse.setOutputList(outputList);
        // 如果每个样例都正常输出，设置 status 为 success
        if (outputList.size() == inputList.size()) {
            executeCodeResponse.setStatus(1);
        }

        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(maxTime);
//        judgeInfo.setMemory();
        executeCodeResponse.setJudgeInfo(judgeInfo);

        // 文件清理
        if (userCodeFile.getParentFile() != null) {
            boolean del = FileUtil.del(userCodePathParentName);
            System.out.println("删除" + (del ? "成功" : "失败"));
        }


        return executeCodeResponse;
    }

    /**
     * 获取错误响应
     * @param e
     * @return
     */
    private ExecuteCodeResponse getErrorResponse(Exception e) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(new ArrayList<>());
        executeCodeResponse.setMessage(e.getMessage());
        // 代码沙箱错误
        executeCodeResponse.setStatus(2);
        executeCodeResponse.setJudgeInfo(new JudgeInfo());
        return executeCodeResponse;
    }
}
