package com.oj.ojbackendmodel.model.dto.question;

import lombok.Data;

/**
 * 判题用例
 *
 * @author jackqiu
 */
@Data
public class JudgeCase {
    /**
     * 输入用例
     */
    private String input;

    /**
     * 输出用例
     */
    private String output;
}
