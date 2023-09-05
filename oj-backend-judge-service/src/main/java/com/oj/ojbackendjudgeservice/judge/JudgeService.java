package com.oj.ojbackendjudgeservice.judge;


import com.oj.ojbackendmodel.model.entity.QuestionSubmit;

/**
 * 判题服务
 * @author jackqiu
 */
public interface JudgeService {

    /**
     * 执行判题逻辑
     * @param questionSubmitId
     */
    QuestionSubmit doJudge(Long questionSubmitId);
}
