package com.yupi.lojbackendquestionservice.controller.inner;

import com.yupi.lojbackendmodel.entity.Question;
import com.yupi.lojbackendmodel.entity.QuestionSubmit;
import com.yupi.lojbackendquestionservice.service.QuestionService;
import com.yupi.lojbackendquestionservice.service.QuestionSubmitService;
import com.yupi.lojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSubmitService questionSubmitService;

    @Override
    @GetMapping("/get/id")
    public Question getQuestionById(@RequestParam("questionId") long questionId) {
        return questionService.getById(questionId);
    }
    @Override
    @PostMapping("/question/update")
    public boolean updateQuestionById(@RequestBody Question question) {
        return questionService.updateById(question);
    }
    @Override
    @GetMapping("/question_submit/get/id")
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionSubmitId") long questionSubmitId) {
        return questionSubmitService.getById(questionSubmitId);
    }
    @Override
    @PostMapping("/question_submit/update")
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit) {
        return questionSubmitService.updateById(questionSubmit);
    }

}
