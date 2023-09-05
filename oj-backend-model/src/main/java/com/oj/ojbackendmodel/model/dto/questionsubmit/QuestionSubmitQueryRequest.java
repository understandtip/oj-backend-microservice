package com.oj.ojbackendmodel.model.dto.questionsubmit;


import com.oj.ojbackendcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 *  
 */
@Data
@EqualsAndHashCode(callSuper = true)//重写父类的方法
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 判题状态(0– 待判题、1–判题中、2–成功、3–失败)
     */
    private Integer status;

    /**
     * 题目id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}