package com.yupi.loj.judge;

import com.yupi.loj.model.entity.QuestionSubmit;
import com.yupi.loj.model.vo.QuestionSubmitVO;

public interface JudgeService {
    QuestionSubmit doJudge(long questionSubmitId);
}
