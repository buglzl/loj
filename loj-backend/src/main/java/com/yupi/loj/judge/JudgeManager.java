package com.yupi.loj.judge;

import com.yupi.loj.judge.strategy.DefaultJudgeStrategy;
import com.yupi.loj.judge.strategy.JavaLanguageJudgeStrategy;
import com.yupi.loj.judge.strategy.JudgeContext;
import com.yupi.loj.judge.strategy.JudgeStrategy;
import com.yupi.loj.judge.codesandbox.model.JudgeInfo;
import com.yupi.loj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理 简化调用
 */
@Service
public class JudgeManager {

    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
