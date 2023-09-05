package com.oj.ojbackendjudgeservice.judge.codesandbox.impl;


import com.oj.ojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.oj.ojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.oj.ojbackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * 第三方代码沙箱
 * @author jackqiu
 */
public class ThirdPartyCodeSandBox implements CodeSandBox {
    /**
     * 真正执行代码的地方
     * @param executeCodeRequest
     * @return
     */
    @Override
    public ExecuteCodeResponse execute(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
