package com.yupi.lojcodesandbox.controller;

import com.yupi.lojcodesandbox.JavaDockerCodeSandbox;
import com.yupi.lojcodesandbox.model.ExecuteCodeRequest;
import com.yupi.lojcodesandbox.model.ExecuteCodeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController("/")
public class MainController {
    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";
    private static final String AUTH_REQUEST_SECRET = "secretKey";

    @Resource
    private JavaDockerCodeSandbox javaDockerCodeSandbox;

    @GetMapping("/health")
    public String healthCheck() {
        return "ok";
    }

    @PostMapping("/executeCode")
    ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request,
                                    HttpServletResponse response) {
        // 基本认证
        String authHeader = request.getHeader(AUTH_REQUEST_HEADER);
        if (!AUTH_REQUEST_SECRET.equals(authHeader)) {
            response.setStatus(403);
            return null;
        }
        if (executeCodeRequest == null) {
            throw new RuntimeException("请求参数为空");
        }
        return javaDockerCodeSandbox.executeCode(executeCodeRequest);
    }
}
