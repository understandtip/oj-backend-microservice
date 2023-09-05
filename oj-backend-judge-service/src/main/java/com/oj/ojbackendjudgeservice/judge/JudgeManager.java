package com.oj.ojbackendjudgeservice.judge;

import com.oj.ojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.oj.ojbackendjudgeservice.judge.strategy.JavaJudgeStrategy;
import com.oj.ojbackendjudgeservice.judge.strategy.JudgeContext;
import com.oj.ojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.oj.ojbackendmodel.model.codesandbox.JudgeInfo;
import com.oj.ojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * @author jackqiu
 */
@Service
public class JudgeManager {
    /**
     * 根据不同的语言执行不同的逻辑
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if("java".equals(language)){
            judgeStrategy = new JavaJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
