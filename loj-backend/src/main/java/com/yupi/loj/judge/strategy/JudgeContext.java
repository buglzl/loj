package com.yupi.loj.judge.strategy;

import com.yupi.loj.model.dto.question.JudgeCase;
import com.yupi.loj.judge.codesandbox.model.JudgeInfo;
import com.yupi.loj.model.entity.Question;
import com.yupi.loj.model.entity.QuestionSubmit;
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
