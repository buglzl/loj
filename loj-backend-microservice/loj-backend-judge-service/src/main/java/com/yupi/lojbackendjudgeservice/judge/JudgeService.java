package com.yupi.lojbackendjudgeservice.judge;


import com.yupi.lojbackendmodel.entity.QuestionSubmit;

public interface JudgeService {
    QuestionSubmit doJudge(long questionSubmitId);
}
