package com.yupi.loj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.loj.annotation.AuthCheck;
import com.yupi.loj.common.BaseResponse;
import com.yupi.loj.common.ErrorCode;
import com.yupi.loj.common.ResultUtils;
import com.yupi.loj.constant.UserConstant;
import com.yupi.loj.exception.BusinessException;
import com.yupi.loj.model.dto.question.QuestionQueryRequest;
import com.yupi.loj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.yupi.loj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.yupi.loj.model.entity.Question;
import com.yupi.loj.model.entity.QuestionSubmit;
import com.yupi.loj.model.entity.User;
import com.yupi.loj.model.vo.QuestionSubmitVO;
import com.yupi.loj.service.QuestionSubmitService;
import com.yupi.loj.service.UserService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 题目提交接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/question_submit")
@Slf4j
@Deprecated
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return 提交记录 id
     */
    @PostMapping("/")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                         HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        // 登录创建题目
        final User loginUser = userService.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }

    /**
     * 分页获取列表（仅管理员）
     * 代码是 用户可以看自己的 管理员可以看所有人的
     * @param questionSubmitQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                         HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();

        // 从数据库中查询原始的提交分页信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));

        User loginUser = userService.getLoginUser(request);
        // 返回脱敏信息
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage, loginUser));
    }


}
