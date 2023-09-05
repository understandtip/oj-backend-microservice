package com.oj.ojbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.ojbackendcommon.common.ErrorCode;
import com.oj.ojbackendcommon.constant.CommonConstant;
import com.oj.ojbackendcommon.exception.BusinessException;
import com.oj.ojbackendcommon.utils.SqlUtils;
import com.oj.ojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.oj.ojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.oj.ojbackendmodel.model.entity.Question;
import com.oj.ojbackendmodel.model.entity.QuestionSubmit;
import com.oj.ojbackendmodel.model.entity.User;
import com.oj.ojbackendmodel.model.enums.QuestionSubmitLanguageEnum;
import com.oj.ojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.oj.ojbackendmodel.model.vo.QuestionSubmitVO;
import com.oj.ojbackendmodel.model.vo.QuestionVO;
import com.oj.ojbackendmodel.model.vo.UserVO;
import com.oj.ojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.oj.ojbackendquestionservice.rabbitmq.MyMessageProducer;
import com.oj.ojbackendquestionservice.service.QuestionService;
import com.oj.ojbackendquestionservice.service.QuestionSubmitService;
import com.oj.ojbackendserviceclient.service.JudgeFeignClient;
import com.oj.ojbackendserviceclient.service.UserFeignClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
* @author jackqiu
* 题目提交服务实现   todo 后续提供封装了事务的方法  doQuestionSubmitInner
 *  todo  每个用户串行点赞,锁必须要包裹住事务方法,  后续再实现
* @description 针对表【question_submit(题目提交)】的数据库操作Service实现
* @createDate 2023-08-16 13:21:44
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService {
    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userService;

    @Resource
    private MyMessageProducer myMessageProducer;

    @Resource
    @Lazy
    private JudgeFeignClient judgeService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        Long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 校验编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setLanguage(languageEnum.getValue());
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setJudgeInfo("{}");
        // 设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setUserId(userId);
        boolean save = this.save(questionSubmit);
        if(!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据插入异常");
        }
        Long questionSubmitId = questionSubmit.getId();
        myMessageProducer.sendMessage("code_exchange","my_routingKey",String.valueOf(questionSubmitId));
        //todo 调用判题服务
//        CompletableFuture.runAsync(() -> {
//            judgeService.doJudge(questionSubmitId);
//        });
        return questionSubmitId;
    }

    /**
     * 获取查询包装类
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
        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取传递给前端的数据(单个)      除了管理员外，普通用户只能看到非答案、提交代码等公开信息）
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        Long questionId = questionSubmit.getQuestionId();
        // 1. 关联查询用户信息
        Long userId = questionSubmit.getUserId();
        //执行脱敏操作    本人或者管理员才能查看
        if(!loginUser.getId().equals(userId) && !userService.isAdmin(loginUser)){
            questionSubmitVO.setCode(null);
        }
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        //关联题目信息
        Question question = null;
        if (questionId != null && questionId > 0) {
            question = questionService.getById(questionId);
        }
        QuestionVO questionVO = questionService.getQuestionVO(question);

        questionSubmitVO.setUserVO(userVO);
        questionSubmitVO.setQuestionVO(questionVO);
        return questionSubmitVO;
    }

    /**
     * 获取传递给前端的数据 (多个)
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(),
                questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        // 1. 关联查询用户信息     Set集合可以去重
        Set<Long> userIdSet = questionSubmitList.stream().map(QuestionSubmit::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        //2.关联题目信息
        Set<Long> questionIdSet = questionSubmitList.stream().map(QuestionSubmit::getQuestionId).collect(Collectors.toSet());
        Map<Long, List<Question>> questionIdUserListMap = questionService.listByIds(questionIdSet).stream()
                .collect(Collectors.groupingBy(Question::getId));
        // 填充信息
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(questionSubmit -> {
            QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
            Long userId = questionSubmit.getUserId();

            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionSubmitVO.setUserVO(userService.getUserVO(user));

            if(!loginUser.getId().equals(userId) && !userService.isAdmin(loginUser)){//执行脱敏操作    本人或者管理员才能查看
                questionSubmitVO.setCode(null);
                UserVO userVO = userService.getUserVO(user);
                userVO.setUserRole(null);//设置了不是本人或者管理员，看不到用户角色
                userVO.setCreateTime(null);//设置了不是本人或者管理员，看不到用户的创建时间
                questionSubmitVO.setUserVO(userVO);//todo  设置了本人不能看到别人的用户信息
            }

            Long questionId = questionSubmit.getQuestionId();
            Question question = null;
            if(questionIdUserListMap.containsKey(questionId)){
                question = questionIdUserListMap.get(questionId).get(0);
            }
            questionSubmitVO.setQuestionVO(questionService.getQuestionVO(question));
            return questionSubmitVO;
        }).collect(Collectors.toList());

        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }
}




