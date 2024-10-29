package io.github.jasonxqh.middleware.sdk;
import io.github.jasonxqh.middleware.sdk.domain.service.impl.OpenAiCodeReviewService;
import io.github.jasonxqh.middleware.sdk.infrastructure.chatbot.IOpenAI;
import io.github.jasonxqh.middleware.sdk.infrastructure.chatbot.impl.ChatGLM;
import io.github.jasonxqh.middleware.sdk.infrastructure.git.GitCommand;
import io.github.jasonxqh.middleware.sdk.infrastructure.weixin.WeiXin;
import io.github.jasonxqh.middleware.sdk.types.utils.BearerTokenUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.logging.Logger;

public class OpenAiCodeReview {


    //配置微信
//    private final String weixin_appid = "wxfa4ad2fa0028f454";
//    private final String weixin_secret = "92958a3c70705e20a7e972a6e568c497";
//    private final String weixin_touser = "oacu-6M5GQpls1XpofHm89TWK40Q";
//    private final String weixin_template_id = "osZKKHx4x3RuZnO41LCkS9AKPgtno0dzKL2W1FILwwU";
//
//    //配置ChatGLM
//    private String chatglm_apiKeySecret= "dfa8338c03d73f7c322b7d99a43dcc91.GE3dUuzWPUYslQEw";
//    private String chatglm_apiHost = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    //Github配置
    private String github_review_log_uri;
    private String github_token;
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
                getEnv("CHATGLM_APIKEYSECRET ")
        );

        OpenAiCodeReviewService openAiCodeReviewService = new OpenAiCodeReviewService(gitCommand, chatGLM, weiXin);
        openAiCodeReviewService.exec();

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
