package com.oj.ojbackendmodel.model.codesandbox;

import lombok.Data;

/**
 * @author jackqiu
 */
@Data
public class JudgeInfo {

    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 消耗时间  单位为ms
     */
    private Long time;

    /**
     * 消耗内存 单位为kb
     */
    private Long memory;
}
