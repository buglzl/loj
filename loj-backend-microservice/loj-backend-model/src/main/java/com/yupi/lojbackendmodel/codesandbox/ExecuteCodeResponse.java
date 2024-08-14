package com.yupi.lojbackendmodel.codesandbox;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteCodeResponse {
    /**
     * 返回输出结果
     */
    private List<String> outputList;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 接口信息
     */
    private String message;

    /**
     * 执行状态
     */
    private Integer status;
}
