package com.oj.ojbackendjudgeservice.judge.strategy;


import com.oj.ojbackendmodel.model.codesandbox.JudgeInfo;

/**
 * 判题策略
 * @author jackqiu
 */
public interface JudgeStrategy {
    /**
     *
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
