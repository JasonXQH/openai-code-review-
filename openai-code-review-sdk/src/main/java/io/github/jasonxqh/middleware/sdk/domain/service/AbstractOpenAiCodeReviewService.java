package io.github.jasonxqh.middleware.sdk.domain.service;

import io.github.jasonxqh.middleware.sdk.infrastructure.chatbot.IOpenAI;
import io.github.jasonxqh.middleware.sdk.infrastructure.git.GitCommand;
import io.github.jasonxqh.middleware.sdk.infrastructure.weixin.WeiXin;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;

/**
 * @author : jasonxu
 * @mailto : xuqihang74@gmail.com
 * @created : 2024/10/29, 星期二
 **/
public abstract class AbstractOpenAiCodeReviewService implements IOpenAiCodeReviewService {
//    private final Logger logger = LoggerFactory.getLogger(AbstractOpenAiCodeReviewService.class);

    protected final GitCommand gitCommand;

    protected final IOpenAI openAI;

    protected final WeiXin weiXin;


    public AbstractOpenAiCodeReviewService(GitCommand gitCommand, IOpenAI openAI, WeiXin weiXin) {
        this.gitCommand = gitCommand;
        this.openAI = openAI;
        this.weiXin = weiXin;
    }

    @Override
    public void exec(){
        try {
            //1. 获取提交代码
            String diffCode = getDiffCode();
            //2. 开始评审代码
            String recommend = codeReview(diffCode);
            //3/ 记录评审结果，返回日志地址
            String logUrl = recordCodeReview(recommend);
            //4.发送消息通知: 日志地址，通知的内容
            pushMessage(logUrl);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    protected abstract String recordCodeReview(String recommend) throws GitAPIException, IOException;

    protected abstract String codeReview(String diffCode) throws Exception;

    protected abstract String getDiffCode() throws IOException, InterruptedException;

    protected abstract void pushMessage(String logUrl);

}
