package com.yupi.lojcodesandbox.utils;

import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.StrUtil;
import com.yupi.lojcodesandbox.model.ExecuteMessage;
import org.apache.tomcat.jni.Proc;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * 进程工具类
 */
public class ProcessUtils {

    public static ExecuteMessage runInteractProcessAndGetMessage(Process runProcess, String args) {
        // 执行命令行
        ExecuteMessage executeMessage = new ExecuteMessage();

        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            InputStream inputStream = runProcess.getInputStream();
            OutputStream outputStream = runProcess.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(args + "\n");

            outputStreamWriter.flush();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            StringBuilder outputStringbuilder = new StringBuilder();
            String outputLine;
            while ((outputLine = bufferedReader.readLine()) != null) {
                outputStringbuilder.append(outputLine);
            }
            executeMessage.setMessage(outputStringbuilder.toString());
            stopWatch.stop();
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
            // 释放资源
            outputStreamWriter.close();
            inputStream.close();
            outputStream.close();
            runProcess.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return executeMessage;
    }
    /**
     * 获取进行运行结果信息
     * @param process
     * @param opt
     * @return
     */
    public static ExecuteMessage runProcessAndGetMessage(Process process, String opt) {
        // 执行命令行
        ExecuteMessage executeMessage = new ExecuteMessage();

        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            Integer exitValue = process.waitFor();
            executeMessage.setExitValue(exitValue);

            if (exitValue == 0) {
                System.out.println(opt + "成功");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder outputStringbuilder = new StringBuilder();
                String outputLine;
                while ((outputLine = bufferedReader.readLine()) != null) {
                    outputStringbuilder.append(outputLine);
                }
                executeMessage.setMessage(outputStringbuilder.toString());
            } else {
                System.out.println(opt + "失败: " + exitValue);
                // 获取正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder outputStringbuilder = new StringBuilder();
                String outputLine;
                while ((outputLine = bufferedReader.readLine()) != null) {
                    outputStringbuilder.append(outputLine);
                }
                executeMessage.setMessage(outputStringbuilder.toString());

                // 获取错误输出
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
                StringBuilder errorOutputStringbuilder = new StringBuilder();
                String errorOutputLine;
                while ((errorOutputLine = errorBufferedReader.readLine()) != null) {
                    errorOutputStringbuilder.append(errorOutputLine);
                }
                executeMessage.setErrorMessage(errorOutputStringbuilder.toString());
            }
            stopWatch.stop();
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return executeMessage;
    }
}
