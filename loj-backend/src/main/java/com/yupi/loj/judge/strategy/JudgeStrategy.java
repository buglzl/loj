package com.yupi.loj.judge.strategy;

import com.yupi.loj.judge.codesandbox.model.JudgeInfo;

/**
 * 判题策略
 */
public interface JudgeStrategy {
    /**
     * 关于返回类型 JudgeInfo，如果后续不满意可以再自定义
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
