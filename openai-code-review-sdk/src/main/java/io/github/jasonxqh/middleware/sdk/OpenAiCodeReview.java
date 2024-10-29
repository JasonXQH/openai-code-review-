package io.github.jasonxqh.middleware.sdk;
import io.github.jasonxqh.middleware.sdk.domain.model.Model;
import io.github.jasonxqh.middleware.sdk.domain.service.impl.OpenAiCodeReviewService;
import io.github.jasonxqh.middleware.sdk.infrastructure.chatbot.IOpenAI;
import io.github.jasonxqh.middleware.sdk.infrastructure.chatbot.impl.ChatGLM;
import io.github.jasonxqh.middleware.sdk.infrastructure.chatbot.impl.Kimi;
import io.github.jasonxqh.middleware.sdk.infrastructure.chatbot.impl.OpenAI;
import io.github.jasonxqh.middleware.sdk.infrastructure.git.GitCommand;
import io.github.jasonxqh.middleware.sdk.infrastructure.weixin.WeiXin;
import io.github.jasonxqh.middleware.sdk.types.utils.BearerTokenUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.util.IO;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.logging.Logger;

public class OpenAiCodeReview {


    //配置微信
    //Github配置
    //工程配置-自动获取
    private String commit_project;
    private String commit_branch;
    private String commit_author;


    public static void main(String[] args) throws IOException, InterruptedException, GitAPIException {
        System.out.println("代码评审，测试执行");
        //1. 代码检出
        GitCommand gitCommand = new GitCommand(
                getEnv("GITHUB_REVIEW_LOG_URI"),
                getEnv("GITHUB_TOKEN"),
                getEnv("COMMIT_PROJECT"),
                getEnv("COMMIT_BRANCH"),
                getEnv("COMMIT_AUTHOR"),
                getEnv("COMMIT_MESSAGE")
        );

        WeiXin weiXin = new WeiXin(
                getEnv("WEIXIN_APPID"),
                getEnv("WEIXIN_SECRET"),
                getEnv("WEIXIN_TOUSER"),
                getEnv("WEIXIN_TEMPLATE_ID")
        );

        IOpenAI chatGLM = new ChatGLM(
                getEnv("CHATGLM_APIHOST"),
//                chatglm_apiHost,
                getEnv("CHATGLM_APIKEYSECRET")
        );
        IOpenAI kimi = new Kimi();
        IOpenAI openAI = new OpenAI();

//        OpenAiCodeReviewService openAiCodeReviewService = new OpenAiCodeReviewService(gitCommand, chatGLM, weiXin);
        OpenAiCodeReviewService openAiCodeReviewService = new OpenAiCodeReviewService(gitCommand, kimi, weiXin);
        Model kimiModel = Model.MOONSHOT_V1_8K;
        openAiCodeReviewService.exec(kimiModel);

        System.out.println("openai-code-review done");
    }

    private  static String getEnv(String key){
        String value = System.getenv(key);
        if(null == value || value.isEmpty()){
            throw new RuntimeException("value is null");
        }
        return value;
    }
}
