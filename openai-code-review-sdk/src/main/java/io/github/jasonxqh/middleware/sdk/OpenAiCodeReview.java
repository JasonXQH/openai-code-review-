package io.github.jasonxqh.middleware.sdk;

import com.alibaba.fastjson2.JSON;
import io.github.jasonxqh.middleware.sdk.domain.model.ChatCompletionRequest;
import io.github.jasonxqh.middleware.sdk.domain.model.ChatCompletionSyncResponseDTO;
import io.github.jasonxqh.middleware.sdk.domain.model.Model;
import io.github.jasonxqh.middleware.sdk.types.utils.BearerTokenUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.Constants.CHARACTERS;

public class OpenAiCodeReview {

    public static void main(String[] args) throws IOException, InterruptedException, GitAPIException {
        System.out.println("代码评审，测试执行");
        //1. 代码检出
        String githubToken = System.getenv("GITHUB_TOKEN");
        if(null == githubToken|| githubToken.isEmpty()) {
            throw new RuntimeException("Token is empty");
        }
        System.out.println("GITHUB_TOKEN: " + githubToken);
        ProcessBuilder processBuilder = new ProcessBuilder("git", "diff", "HEAD~1", "HEAD");
        processBuilder.directory(new File("."));

        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        StringBuilder diffCode = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            diffCode.append(line);
        }

        int exitCode = process.waitFor();
        System.out.println("Exit with code: " + exitCode);

        System.out.println("diff code： "+diffCode);

        //2. chatglm 代码评审
        String log = codeReview(diffCode.toString());
        System.out.println("code review: " + log);
        //写入评审日志
        String logUrl = writeLog(githubToken, log);
        System.out.println("writeLog: "+logUrl);
    }

    private static String codeReview(String diffCode) throws IOException {
        String apiKey = "dfa8338c03d73f7c322b7d99a43dcc91.GE3dUuzWPUYslQEw";
        String token = BearerTokenUtils.getToken(apiKey);
        System.out.println(token);
        HttpURLConnection urlConnection = getHttpURLConnection(token,diffCode);

        int responseCode = urlConnection.getResponseCode();
        System.out.println("Response code: " + responseCode);
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String inputLine;

        StringBuilder response = new StringBuilder();

        while((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }

        reader.close();
        urlConnection.disconnect();


        ChatCompletionSyncResponseDTO chatCompletionSyncResponse = JSON.parseObject(response.toString(), ChatCompletionSyncResponseDTO.class);
        return chatCompletionSyncResponse.getChoices().get(0).getMessage().getContent();
    }

    private static HttpURLConnection getHttpURLConnection(String token,String diffCode) throws IOException {
        URL url = new URL("https://open.bigmodel.cn/api/paas/v4/chat/completions");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Authorization", "Bearer " + token);
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        urlConnection.setDoOutput(true);


        ChatCompletionRequest chatCompletionRequest = new ChatCompletionRequest();
        chatCompletionRequest.setModel(Model.GLM_4_FLASH.getCode());
        chatCompletionRequest.setMessages(new ArrayList<ChatCompletionRequest.Prompt>(){
            private static final long serialVersionUID = -7988151926241837899L;
            {
                add(new ChatCompletionRequest.Prompt("user", "你是一位资深编程专家，拥有深厚的编程基础和广泛的技术栈知识。你的专长在于识别代码中的低效模式、安全隐患、以及可维护性问题，并能提出针对性的优化策略。你擅长以易于理解的方式解释复杂的概念，确保即使是初学者也能跟随你的指导进行有效改进。在提供优化建议时，你注重平衡性能、可读性、安全性、逻辑错误、异常处理、边界条件，以及可维护性方面的考量，同时尊重原始代码的设计意图。\n" +
                        "你总是以鼓励和建设性的方式提出反馈，致力于提升团队的整体编程水平，详尽指导编程实践，雕琢每一行代码至臻完善。用户会将仓库代码分支修改代码给你，以git diff 字符串的形式提供，你需要根据变化的代码，帮忙review本段代码。然后你review内容的返回内容必须严格遵守下面我给你的格式，包括标题内容。\n" +
                        "模板中的变量内容解释：\n" +
                        "变量1是给review打分，分数区间为0~100分。\n" +
                        "变量2 是code review发现的问题点，包括：可能的性能瓶颈、逻辑缺陷、潜在问题、安全风险、命名规范、注释、以及代码结构、异常情况、边界条件、资源的分配与释放等等\n" +
                        "变量3是具体的优化修改建议。\n" +
                        "变量4是你给出的修改后的代码。 \n" +
                        "变量5是代码中的优点。\n" +
                        "变量6是代码的逻辑和目的，识别其在特定上下文中的作用和限制\n" +
                        "\n" +
                        "必须要求：\n" +
                        "1. 以精炼的语言、严厉的语气指出存在的问题。\n" +
                        "2. 你的反馈内容必须使用严谨的markdown格式\n" +
                        "3. 不要携带变量内容解释信息。\n" +
                        "4. 有清晰的标题结构\n" +
                        "返回格式严格如下：\n" +
                        "# 小傅哥项目： OpenAi 代码评审.\n" +
                        "### \uD83D\uDE00代码评分：{变量1}\n" +
                        "#### \uD83D\uDE00代码逻辑与目的：\n" +
                        "{变量6}\n" +
                        "#### ✅代码优点：\n" +
                        "{变量5}\n" +
                        "#### \uD83E\uDD14问题点：\n" +
                        "{变量2}\n" +
                        "#### \uD83C\uDFAF修改建议：\n" +
                        "{变量3}\n" +
                        "#### \uD83D\uDCBB修改后的代码：\n" +
                        "{变量4}\n" +
                        "`;代码如下:"));
                add(new ChatCompletionRequest.Prompt("user", diffCode));
            }
        });

        try(OutputStream os = urlConnection.getOutputStream()) {
            byte[] bytes = JSON.toJSONString(chatCompletionRequest).getBytes("UTF-8");
            os.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return urlConnection;
    }
    private static String writeLog(String token ,String log) throws IOException, GitAPIException {
        Git git = Git.cloneRepository()
                .setURI("https://github.com/JasonXQH/openai-code-review-log.git")
                .setDirectory(new File("repo"))
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, ""))
                .call();

        String dateFolderName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        File dateFolder = new File("repo/"+dateFolderName);
        if(!dateFolder.exists()){
            dateFolder.mkdirs();
        }


        String fileName = generateRandomString(12)+".md";
        File newFile = new File(dateFolder, fileName);
        try(FileWriter fw = new FileWriter(newFile)) {
            fw.write(log);
        }

        git.add().addFilepattern(dateFolderName+"/"+fileName).call();
        System.out.println("git add 完成");
        // 获取并打印 git 状态
        StatusCommand statusCommand = git.status();
        Status status = statusCommand.call();
        System.out.println("Added files: " + status.getAdded());
        System.out.println("Changed files: " + status.getChanged());
        System.out.println("Untracked files: " + status.getUntracked());
        git.commit().setMessage("Add new File").call();
        System.out.println("git commit 完成");
        git.push().setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, "")).call();
        System.out.println("git push 完成");
        return "https://github.com/JasonXQH/openai-code-review-log/blob/master/"+dateFolderName+"/"+fileName;
    }
    // 生成指定长度的随机字符串
    private static String generateRandomString(int length) {
        // 定义字符集
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            // 随机选取字符
            int index = random.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(index));
        }
        return stringBuilder.toString();
    }
}
