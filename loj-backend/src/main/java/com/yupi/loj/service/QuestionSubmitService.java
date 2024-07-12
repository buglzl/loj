package com.yupi.loj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.loj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.yupi.loj.model.entity.QuestionSubmit;
import com.yupi.loj.model.entity.User;

/**
* @author 28681
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2024-07-11 10:52:47
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 提交
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

}
