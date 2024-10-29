package io.github.jasonxqh.middleware.sdk.test;

import com.alibaba.fastjson2.JSON;
import io.github.jasonxqh.middleware.sdk.infrastructure.chatbot.dto.ChatCompletionSyncResponseDTO;
import io.github.jasonxqh.middleware.sdk.infrastructure.weixin.dto.TemplateMessageDTO;
import io.github.jasonxqh.middleware.sdk.types.utils.BearerTokenUtils;
import io.github.jasonxqh.middleware.sdk.types.utils.WXAccessTokenUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ApiTest {
    //dfa8338c03d73f7c322b7d99a43dcc91.GE3dUuzWPUYslQEw
    @Test
    public void test() {
        String apiKey = "dfa8338c03d73f7c322b7d99a43dcc91.GE3dUuzWPUYslQEw";
        String token = BearerTokenUtils.getToken(apiKey);
        System.out.println(token);
    }

    @Test
    public void test_http() throws IOException {
        String token = "sk-jOjzYzUyMte5M2sGWDSZaSGpTwvhRVGomKRgbgstvYCEr1aQ";
        URL url = new URL("https://api.moonshot.cn/v1/chat/completions");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Authorization", "Bearer " + token);
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        urlConnection.setDoOutput(true);

        String code = "1+1";

        String jsonInpuString = "{"
                + "\"model\":\"moonshot-v1-8k\","
                + "\"messages\": ["
                + "    {"
                + "        \"role\": \"user\","
                + "        \"content\": \"你是一个高级编程架构师，精通各类场景方案、架构设计和编程语言请，请您根据git diff记录，对代码做出评审。代码为: " + code + "\""
                + "    }"
                + "]"
                + "}";

        try(OutputStream os = urlConnection.getOutputStream()) {
            byte[] bytes = jsonInpuString.getBytes("UTF-8");
            os.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int responseCode = urlConnection.getResponseCode();
        System.out.println(responseCode);

        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String inputLine;

        StringBuilder response = new StringBuilder();

        while((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }

        reader.close();
        urlConnection.disconnect();

        System.out.println(response);

        ChatCompletionSyncResponseDTO chatCompletionSyncResponse = JSON.parseObject(response.toString(), ChatCompletionSyncResponseDTO.class);
        System.out.println(chatCompletionSyncResponse.getChoices().get(0).getMessage().getContent());
    }

    @Test
    public void test_wx(){
        String accessToken = WXAccessTokenUtils.getAccessToken();
        System.out.println(accessToken);
        TemplateMessageDTO weixinTemplateMessageDTO = new TemplateMessageDTO();
        weixinTemplateMessageDTO.put("project","big-market");
        weixinTemplateMessageDTO.put("review","feat: 新加功能");
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+accessToken;
        sendPostRequest(url,JSON.toJSONString(weixinTemplateMessageDTO));

    }


    private static void sendPostRequest(String urlString, String jsonBody) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
                String response = scanner.useDelimiter("\\A").next();
                System.out.println(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}