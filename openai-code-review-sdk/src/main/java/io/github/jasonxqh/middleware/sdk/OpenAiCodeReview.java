package io.github.jasonxqh.middleware.sdk;

import com.alibaba.fastjson2.JSON;
import io.github.jasonxqh.middleware.sdk.domain.model.ChatCompletionSyncResponseDTO;
import io.github.jasonxqh.middleware.sdk.types.utils.BearerTokenUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenAiCodeReview {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("测试执行");
        //1. 代码检出

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
    }

    private static String codeReview(String diffCode) throws IOException {
        String apiKey = "dfa8338c03d73f7c322b7d99a43dcc91.GE3dUuzWPUYslQEw";
        String token = BearerTokenUtils.getToken(apiKey);

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

        String jsonInpuString = "{"
                + "\"model\":\"glm-4-flash\","
                + "\"messages\": ["
                + "    {"
                + "        \"role\": \"user\","
                + "        \"content\": \"你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码为: " + diffCode + "\""
                + "    }"
                + "]"
                + "}";

        try(OutputStream os = urlConnection.getOutputStream()) {
            byte[] bytes = jsonInpuString.getBytes("UTF-8");
            os.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return urlConnection;
    }
}
