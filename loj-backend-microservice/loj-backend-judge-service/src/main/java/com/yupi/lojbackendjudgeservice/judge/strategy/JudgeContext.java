package com.yupi.lojbackendjudgeservice.judge.strategy;


import com.yupi.lojbackendmodel.codesandbox.JudgeInfo;
import com.yupi.lojbackendmodel.dto.question.JudgeCase;
import com.yupi.lojbackendmodel.entity.Question;
import com.yupi.lojbackendmodel.entity.QuestionSubmit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 上下文 用于传递需要判断的信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JudgeContext {
    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;
}
