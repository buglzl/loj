package com.yupi.lojbackendjudgeservice.judge;

import com.yupi.lojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.yupi.lojbackendjudgeservice.judge.strategy.JavaLanguageJudgeStrategy;
import com.yupi.lojbackendjudgeservice.judge.strategy.JudgeContext;
import com.yupi.lojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.yupi.lojbackendmodel.codesandbox.JudgeInfo;
import com.yupi.lojbackendmodel.entity.QuestionSubmit;
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
