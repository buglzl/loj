package com.yupi.lojbackendquestionservice.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.lojbackendcommon.common.ErrorCode;
import com.yupi.lojbackendcommon.constant.CommonConstant;
import com.yupi.lojbackendcommon.exception.BusinessException;
import com.yupi.lojbackendcommon.utils.SqlUtils;
import com.yupi.lojbackendmodel.dto.questionsubmit.QuestionSubmitAddRequest;
import com.yupi.lojbackendmodel.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.yupi.lojbackendmodel.entity.Question;
import com.yupi.lojbackendmodel.entity.QuestionSubmit;
import com.yupi.lojbackendmodel.entity.User;
import com.yupi.lojbackendmodel.enums.QuestionSubmitLanguageEnum;
import com.yupi.lojbackendmodel.enums.QuestionSubmitStatusEnum;
import com.yupi.lojbackendmodel.vo.QuestionSubmitVO;
import com.yupi.lojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.yupi.lojbackendquestionservice.rabbmitmq.MyMessageProducer;
import com.yupi.lojbackendquestionservice.service.QuestionService;
import com.yupi.lojbackendquestionservice.service.QuestionSubmitService;
import com.yupi.lojbackendserviceclient.service.JudgeFeignClient;
import com.yupi.lojbackendserviceclient.service.UserFeignClient;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 28681
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2024-07-11 10:52:47
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {
    @Resource
    private QuestionService questionService;
    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private MyMessageProducer myMessageProducer;

    @Resource
    private QuestionSubmitMapper questionSubmitMapper;

    @Resource
    @Lazy
    private JudgeFeignClient judgeFeignClient;

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
        // 计算用户提交题目且等待数量是否为 0,是就拒绝提交
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId).eq("questionId", questionId).in("status", Arrays.asList(QuestionSubmitStatusEnum.WAITING.getValue(), QuestionSubmitStatusEnum.RUNNING.getValue()));
        List<QuestionSubmit> questionSubmits = questionSubmitMapper.selectList(queryWrapper);
        if (!questionSubmits.isEmpty()) {
            throw new BusinessException(ErrorCode.RATE_LIMIT_EXCEEDED, "已经有题目在提交等待中或者运行中");
        }
        // 每个用户串行提交题目
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setUserId(userId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
//        // todo 设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");

//        System.out.println(JSONUtil.toJsonStr(questionSubmit));

        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统提交失败");
        }
        // 这个要保存一下提交数量
        question.setSubmitNum(question.getSubmitNum() + 1);
        questionService.updateById(question);
        // todo 实际对提交进行处理
        // 执行判题服务
        Long questionSubmitId = questionSubmit.getId();
        // 发送消息
        myMessageProducer.sendMessage("code_exchange", "my_routingKey", String.valueOf(questionSubmitId));
        // 异步操作
//        CompletableFuture.runAsync(() -> {
//            judgeFeignClient.doJudge(questionSubmitId);
//        });
        return questionSubmit.getId();
    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到mybatis支持的QueryWrapper类）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        queryWrapper.eq(ObjectUtils.isNotEmpty(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_DESC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏 过滤代码
        long userId = loginUser.getId();
        if (userId != questionSubmit.getUserId() && !userFeignClient.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollUtil.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(questionSubmit -> {
            return getQuestionSubmitVO(questionSubmit, loginUser);
        }).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }
}




