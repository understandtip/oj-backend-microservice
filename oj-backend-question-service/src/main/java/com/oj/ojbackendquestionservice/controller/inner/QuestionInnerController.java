package com.oj.ojbackendquestionservice.controller.inner;

import com.oj.ojbackendmodel.model.entity.Question;
import com.oj.ojbackendmodel.model.entity.QuestionSubmit;
import com.oj.ojbackendquestionservice.service.QuestionService;
import com.oj.ojbackendquestionservice.service.QuestionSubmitService;
import com.oj.ojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author jackqiu
 */
@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Override
    @GetMapping("/get/id")
    public Question getQuestionById(@RequestParam("questionId") long questionId){
        return questionService.getById(questionId);
    }

    @Override
    @GetMapping("/question_submit/get/id")
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionId") long questionSubmitId){
        return questionSubmitService.getById(questionSubmitId);
    }

    @Override
    @PostMapping("/question_submit/update")
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit){
        return questionSubmitService.updateById(questionSubmit);
    }

    @Override
    @PostMapping("/update/id")
    public boolean updateQuestionById(@RequestBody Question question) {
        return questionService.updateById(question);
    }
}
