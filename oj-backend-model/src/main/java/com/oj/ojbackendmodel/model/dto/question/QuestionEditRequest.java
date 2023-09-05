package com.oj.ojbackendmodel.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 编辑请求
 *
 *  
 */
@Data
public class QuestionEditRequest implements Serializable {

    /**
     * id  需要根据id 来定位需要进行编辑的数据
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表  List集合方便前端传递数据到后端
     */
    private List<String> tags;

    /**
     * 题目答案
     */
    private String answer;

    /**
     * 判题用例(json数组)
     */
    private List<JudgeCase> judgeCase;

    /**
     * 判题配置(json对象)
     */
    private JudgeConfig judgeConfig;

    private static final long serialVersionUID = 1L;
}