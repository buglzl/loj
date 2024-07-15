package com.yupi.loj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.loj.model.dto.question.QuestionQueryRequest;
import com.yupi.loj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.yupi.loj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.yupi.loj.model.entity.Question;
import com.yupi.loj.model.entity.QuestionSubmit;
import com.yupi.loj.model.entity.User;
import com.yupi.loj.model.vo.QuestionSubmitVO;
import com.yupi.loj.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

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

    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);


    /**
     * 获取题目封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目封装
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);

}
