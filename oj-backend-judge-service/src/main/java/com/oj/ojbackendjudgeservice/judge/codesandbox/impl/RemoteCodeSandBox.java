package com.oj.ojbackendjudgeservice.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.oj.ojbackendcommon.common.ErrorCode;
import com.oj.ojbackendcommon.exception.BusinessException;
import com.oj.ojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.oj.ojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.oj.ojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 远端代码沙箱   （自己实现的代码沙箱逻辑）
 * @author jackqiu
 */
@Component
public class RemoteCodeSandBox implements CodeSandBox {

    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";

    @Value("${codesandbox.url}")
    private String url;

    /**
     * 真正执行代码的地方
     * @param executeCodeRequest
     * @return
     */
    @Override
    public ExecuteCodeResponse execute(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远端代码沙箱");
        String json = JSONUtil.toJsonStr(executeCodeRequest);
//        String url = "http://localhost:8090/executeCode";
        String url = "http://47.120.11.50:8090/executeCode";
//        String url = "http://192.168.59.130:8090/executeCode";
        String response = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER,AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();
        if(StringUtils.isBlank(response)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR,"远程接口调用失败 : " + response);
        }
        return JSONUtil.toBean(response, ExecuteCodeResponse.class);
    }
}
