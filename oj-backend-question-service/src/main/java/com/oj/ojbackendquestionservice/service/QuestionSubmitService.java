package com.oj.ojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.oj.ojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.oj.ojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.oj.ojbackendmodel.model.entity.QuestionSubmit;
import com.oj.ojbackendmodel.model.entity.User;
import com.oj.ojbackendmodel.model.vo.QuestionSubmitVO;

/**
* @author jackqiu
 * 题目提交服务
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2023-08-16 13:21:44
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 题目提交
     *
     * @param questionSubmitAddRequest  前端传递过来的信息
     * @param loginUser
     * @return
     */
    Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);
    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    /**
     * 获取题目提交封装
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    /**
     * 分页获取题目提交封装
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser);
}
