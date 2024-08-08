package com.yupi.lojcodesandbox;


import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.AttachContainerResultCallback;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import com.yupi.lojcodesandbox.model.ExecuteCodeRequest;
import com.yupi.lojcodesandbox.model.ExecuteCodeResponse;
import com.yupi.lojcodesandbox.model.ExecuteMessage;
import com.yupi.lojcodesandbox.model.JudgeInfo;
import com.yupi.lojcodesandbox.security.DefaultSecurityManager;
import com.yupi.lojcodesandbox.utils.ProcessUtils;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JavaDockerCodeSandbox extends JavaCodeSandboxTemplate{
    private static final Boolean FIRST_INIT = true;

    private static final long TIME_OUT = 10000L;



    public static void main(String[] args) {
        CodeSandbox codeSandbox = new JavaDockerCodeSandbox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
//        String code = ResourceUtil.readStr("testCode/unsafeCode/RunFileError.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/unsafeCode/WriteFileError.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/unsafeCode/ReadFileError.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/unsafeCode/MemoryError.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/unsafeCode/SleepError.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/SimpleComputeArgs/Main.java", StandardCharsets.UTF_8);
        String code = ResourceUtil.readStr("testCode/unsafeCode/SegmentFault.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("testCode/SimpleCompute/Main.java", StandardCharsets.UTF_8);
        executeCodeRequest.setLanguage("java");
        executeCodeRequest.setCode(code);
        executeCodeRequest.setInputList(Arrays.asList("3\n", "6\n"));
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }

    /**
     * 将输入参数转化成 echo 管道模式的标准输入
     * @param inputArgs
     * @return
     */
    public String[] getCmd(String inputArgs) {

        String replacedStr = inputArgs.toString().replace("\n", "\\n");
        return new String[]{"sh", "-c", "echo -e \"" + replacedStr + "\" | java -Dfile.encoding=UTF-8 -cp /app Main"};
    }

    @Override
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        String userCodePathParentName = userCodeFile.getParentFile().getAbsolutePath();
        // 创建容器，把文件复制到容器内
        DockerClient dockerClient = DockerClientBuilder
                .getInstance()
                .build();
//        PingCmd pingCmd = dockerClient.pingCmd();
//        pingCmd.exec();
        String image = "openjdk:8-alpine";
        if (FIRST_INIT) {
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    System.out.println("下载镜像：" + item.getStatus());
                    super.onNext(item);
                }
            };
            try {
                pullImageCmd
                        .exec(pullImageResultCallback)
                        .awaitCompletion();
            } catch (InterruptedException e) {
                System.out.println("拉取镜像异常");
                throw new RuntimeException(e);
            }
        }
        System.out.println("下载完成");

        // 创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);

        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(100 * 1024 * 1024L);
        hostConfig.withMemorySwap(0L);
        hostConfig.withCpuCount(1L);
        hostConfig.setBinds(new Bind(userCodePathParentName, new Volume("/app")));
        CreateContainerResponse createContainerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withNetworkDisabled(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withTty(true)
                .exec();
        System.out.println(createContainerResponse);
        String containerId = createContainerResponse.getId();


        // 启动容器
        dockerClient.startContainerCmd(containerId).exec();

        //  docker exec silly_chatelet java -cp /app Main 3 4
        // 获取结果
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs: inputList) {

            String[] cmdArray = getCmd(inputArgs);
//            String[] cmdArray = {"sh", "-c", "echo -e \"1\\n3 2 6\\n\" | java -cp /app Main"};
            System.out.println("最终cmd: " + Arrays.toString(cmdArray));

            // 用来计算程序运行时间
            StopWatch stopWatch = new StopWatch();

            ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                    .withCmd(cmdArray)
                    .withTty(true)
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .withAttachStdout(true)
                    .exec();
            System.out.println("创建执行命令： " + execCreateCmdResponse);

            ExecuteMessage executeMessage = new ExecuteMessage();
            StringBuilder errorMessageSB = new StringBuilder();
            StringBuilder messageSB = new StringBuilder();
            ByteArrayOutputStream stderrBuffer = new ByteArrayOutputStream();
            ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
            long time = 0L;
            final Boolean[] timeout = {true};
            final Long[] maxMemory = {0L};
            final boolean[] stderrFirst = {true};
            final boolean[] stdoutFirst = {true};

            String execId = execCreateCmdResponse.getId();
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback() {
                @Override
                public void onComplete() {
                    // 如果执行完成，说明没超时
                    timeout[0] = false;
                    // errorMessageSB.append(new String(stderrBuffer.toByteArray(), StandardCharsets.UTF_8));
                    // messageSB.append(new String(stdoutBuffer.toByteArray(), StandardCharsets.UTF_8));
                    super.onComplete();
                }

                @Override
                public void onNext(Frame frame) {
                    StreamType streamType = frame.getStreamType();

                    // System.out.println("输出流：" + streamType);
                    if (streamType.STDERR.equals(streamType)) {
                        try {
                            byte[] payload = frame.getPayload();
                            stderrBuffer.write(payload);
                            if (!stderrFirst[0]) {
                                errorMessageSB.append(new String(stderrBuffer.toByteArray(), StandardCharsets.UTF_8));
                                stderrBuffer.reset();
                            }
                            if (stderrFirst[0]) {
                                stderrFirst[0] = false;
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
//                        System.out.println("执行代码，输出错误信息： " + errorMessageSB);
                    } else {
                        try {
                            byte[] payload = frame.getPayload();
                            stdoutBuffer.write(payload);
                            if (!stdoutFirst[0]) {
                                messageSB.append(new String(stdoutBuffer.toByteArray(), StandardCharsets.UTF_8));
                                stdoutBuffer.reset();
                            }
                            if (stdoutFirst[0]) {
                                stdoutFirst[0] = false;
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
//                        System.out.println("执行代码，输出结果：" + messageSB);
                    }
                    super.onNext(frame);
                }
            };

            // 获取占用内存
            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            ResultCallback<Statistics> statisticsResultCallback = statsCmd.exec(new ResultCallback<Statistics>() {
                @Override
                public void onStart(Closeable closeable) {

                }

                @Override
                public void onNext(Statistics statistics) {
                    System.out.println("内存占用：" + statistics.getMemoryStats().getUsage());
                    maxMemory[0] = Math.max(statistics.getMemoryStats().getUsage(), maxMemory[0]);
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onComplete() {

                }

                @Override
                public void close() throws IOException {

                }
            });
            statsCmd.exec(statisticsResultCallback);

            try {
                // 开始监测
                stopWatch.start();
                dockerClient.execStartCmd(execId)
                        .withTty(true)
                        .exec(execStartResultCallback)
                        .awaitCompletion(TIME_OUT, TimeUnit.MILLISECONDS);
                stopWatch.stop();
                time = stopWatch.getLastTaskTimeMillis();
                statsCmd.close();
            } catch (InterruptedException e) {
                System.out.println("程序执行异常");
                throw new RuntimeException(e);
            }

            executeMessage.setErrorMessage(errorMessageSB.toString());
            System.out.println("输出结果：" + messageSB);
            executeMessage.setMessage(messageSB.toString());
            executeMessage.setTime(time);
            executeMessage.setMemory(maxMemory[0]);
            executeMessageList.add(executeMessage);
        }
        dockerClient.stopContainerCmd(containerId).exec();
        dockerClient.removeContainerCmd(containerId).exec();
        return executeMessageList;
    }

    @Override
    public ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessageList) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();
        Long maxTime = 0L;
        Long maxMemory = 0L;
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
            Long memory = executeMessage.getMemory();
            if (time != null) {
                maxTime = Math.max(maxTime, time);
                maxMemory = Math.max(maxMemory, memory);
            }
        }

        executeCodeResponse.setOutputList(outputList);
        // 如果每个样例都正常输出，设置 status 为 success
        if (outputList.size() == executeMessageList.size()) {
            executeCodeResponse.setStatus(1);
        }

        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(maxTime);
        judgeInfo.setMemory(maxMemory);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
}