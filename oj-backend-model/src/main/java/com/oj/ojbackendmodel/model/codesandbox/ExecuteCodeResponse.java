package com.oj.ojbackendmodel.model.codesandbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 代码沙箱执行代码之后返回的响应信息
 * @author jackqiu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {
    /**
     * 代码执行状态
     */
    private String status;

    /**
     * 代码执行信息   todo 展示系统错误的一些信息
     */
    private String message;

    /**
     * 代码执行信息(程序执行信息   |   消耗时间(单位为ms)   |  消耗内存(单位为kb))
     */
    private JudgeInfo judgeInfo;

    /**
     * 代码实际输出用例
     */
    private List<String> output;
}
