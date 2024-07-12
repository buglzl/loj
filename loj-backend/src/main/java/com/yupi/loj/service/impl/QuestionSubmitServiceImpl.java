package com.yupi.loj.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.loj.common.ErrorCode;
import com.yupi.loj.exception.BusinessException;
import com.yupi.loj.model.dto.questionsubmit.JudgeInfo;
import com.yupi.loj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.yupi.loj.model.entity.Question;
import com.yupi.loj.model.entity.QuestionSubmit;
import com.yupi.loj.model.entity.QuestionSubmit;
import com.yupi.loj.model.entity.User;
import com.yupi.loj.model.enums.QuestionSubmitLanguageEnum;
import com.yupi.loj.model.enums.QuestionSubmitStatusEnum;
import com.yupi.loj.service.QuestionService;
import com.yupi.loj.service.QuestionSubmitService;
import com.yupi.loj.service.QuestionSubmitService;
import com.yupi.loj.mapper.QuestionSubmitMapper;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
* @author 28681
* @description 针对表【question_submit(题目提交)】的数据库操作Service实现
* @createDate 2024-07-11 10:52:47
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService{
    @Resource
    private QuestionService questionService;

    /**
     * 点赞
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return 提交记录 id
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 判断编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        // 判断实体是否存在，根据类别获取实体
        long questionId = questionSubmitAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已经提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setUserId(userId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        // todo 设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WATTING.getValue());
        questionSubmit.setJudgeInfo("{}");
        System.out.println(JSONUtil.toJsonStr(questionSubmit));

        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统提交失败");
        }
        return questionSubmit.getId();
    }


}




