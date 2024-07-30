package com.yupi.lojcodesandbox.security;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TestSecurityManager {
    public static void main(String[] args) {
        
        System.setSecurityManager(new MySecurityManager());
        List<String> strings = FileUtil.readLines("E:\\Java_workstation\\LOJ\\loj-code-sandbox\\src\\main\\resources\\application.yml", StandardCharsets.UTF_8);
        System.out.println(strings);
    }
}
