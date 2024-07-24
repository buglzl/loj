package com.yupi.loj.judge.codesandbox.model;

import lombok.Data;

@Data
public class JudgeInfo {
    /**
     * 程序执行信息
     */
    private String message;
    /**
     * 时间消耗（ms）
     */
    private Long time;
    /**
     * 内存消耗（kb）
     */
    private Long memory;
}
