package com.yupi.lojbackendmodel.codesandbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteCodeRequest {
    /**
     * 编程语言
     */
    private String language;
    /**
     * 代码
     */
    private String code;
    /**
     * 输入测试列表
     */
    private List<String> inputList;
}
