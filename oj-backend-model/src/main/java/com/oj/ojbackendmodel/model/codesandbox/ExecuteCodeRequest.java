package com.oj.ojbackendmodel.model.codesandbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 代码服务传递给代码沙箱的请求参数信息
 * @author jackqiu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeRequest {
    /**
     * 用户提交的代码
     */
    private String code;

    /**
     * 题目的输入用例
     */
    private List<String> inputList;

    /**
     * 代码使用的语言
     */
    private String language;
}
