package com.yupi.lojcodesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteMessage {
    /**
     * 程序退出代码
     */
    private Integer exitValue;
    /**
     * 返回的错误信息
     */
    private String errorMessage;
    /**
     * 返回的正确信息
     */
    private String message;

    private Long time;
    private Long memory;
}
