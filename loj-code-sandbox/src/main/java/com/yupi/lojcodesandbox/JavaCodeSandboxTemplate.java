package com.yupi.lojcodesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.yupi.lojcodesandbox.model.ExecuteCodeRequest;
import com.yupi.lojcodesandbox.model.ExecuteCodeResponse;
import com.yupi.lojcodesandbox.model.ExecuteMessage;
import com.yupi.lojcodesandbox.model.JudgeInfo;
import com.yupi.lojcodesandbox.security.DefaultSecurityManager;
import com.yupi.lojcodesandbox.utils.ProcessUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
public abstract class JavaCodeSandboxTemplate implements CodeSandbox{
    private static final String GLOBAL_DIR = "tmpCode";
    private static final String GLOBAL_CLASS = "Main.java";
    private static final String rootPathName;
    private static final String globalCodePathName;
    private static final List<String> blackList = Arrays.asList("Files", "exec");

    private static final long TIME_OUT = 10000L;

    /**
     * 判断项目下是否有 tmpCode 这个文件，如果没有就创建。
     * 另外，如果想要将代码放到其他位置，修改 user.dir 即可
     */
    static {
        // 由于这段代码每次调用这个函数的时候都会有，因此设置为静态块
        rootPathName = System.getProperty("user.dir");
        globalCodePathName = rootPathName + File.separator + GLOBAL_DIR;
        // 判断该文件夹是否存在 注意这个文件
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }
    }

    /**
     * 将用户的代码存储到一个随机文件夹下，然后将其保存成 java 文件
     * 返回其文件(File) 对象
     * @param code
     * @return
     */
    public File saveCodeToFile(String code) {
        String userCodePathParentName = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodePathParentName + File.separator + GLOBAL_CLASS;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);
        return userCodeFile;
    }

    /**
     * 编译代码，得到编译文件以及相应信息
     * @param userCodeFile
     * @return
     */
    public ExecuteMessage compileFile(File userCodeFile) {
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsoluteFile());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeCompileMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            System.out.println(executeCompileMessage);
            return executeCompileMessage;
        } catch (IOException e) {
            // 编译出了问题，后续需要处理
            // return getErrorResponse(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 执行文件，返回执行结果列表
     * @param userCodeFile
     * @param inputList
     * @return
     */
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        String userCodePathParentName = userCodeFile.getParentFile().getAbsolutePath();
        System.out.println("父文件夹：" + userCodePathParentName);
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
                throw new RuntimeException("执行错误", e);
             //   return getErrorResponse(e);
            }
        }
        return executeMessageList;
    }

    /**
     * 根据执行结果信息，返回输出结果
     * @param executeMessageList
     * @return
     */
    public  ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessageList) {
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
        if (outputList.size() == executeMessageList.size()) {
            executeCodeResponse.setStatus(1);
        }

        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(maxTime);
//        judgeInfo.setMemory();
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }

    /**
     * 删除文件
     * @param userCodeFile
     * @return
     */
    public boolean deleteFile(File userCodeFile) {
        if (userCodeFile.getParentFile() != null) {
            String userCodePathParentName = userCodeFile.getParentFile().getAbsolutePath();
            boolean del = FileUtil.del(userCodePathParentName);
            System.out.println("删除" + (del ? "成功" : "失败"));
            return del;
        }
        return true;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {

        String language = executeCodeRequest.getLanguage();
        String code = executeCodeRequest.getCode();
        List<String> inputList = executeCodeRequest.getInputList();


        // 1. 把用户的文件放在某个确定的文件夹下
        File userCodeFile = saveCodeToFile(code);

        // 2. 编译代码
        ExecuteMessage compileMessage = compileFile(userCodeFile);
        System.out.println(compileMessage);
        if (compileMessage.getExitValue() == 0) {

            // 注意，如果编译失败就不能往下来，日后还需要改这部分代码
            // 3. 运行代码
            List<ExecuteMessage> executeMessageList = runFile(userCodeFile, inputList);
            // 4. 获取输出
            ExecuteCodeResponse outputResponse = getOutputResponse(executeMessageList);
            // 5. 文件清理
            boolean b = deleteFile(userCodeFile);
            if (!b) {
                log.error("deleteFile error, userCodeFilePath = {}", userCodeFile.getAbsolutePath());
            }
            return outputResponse;
        }

        ExecuteCodeResponse compileErrorResponse = new ExecuteCodeResponse();
        compileErrorResponse.setMessage("compile error");

        // 5. 文件清理
        boolean b = deleteFile(userCodeFile);
        if (!b) {
            log.error("deleteFile error, userCodeFilePath = {}", userCodeFile.getAbsolutePath());
        }
        return compileErrorResponse;
    }

    /**
     * 6. 获取错误响应
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
