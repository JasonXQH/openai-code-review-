package io.github.jasonxqh.middleware.sdk.infrastructure.chatbot.impl;

import com.alibaba.fastjson2.JSON;
import io.github.jasonxqh.middleware.sdk.domain.model.Model;
import io.github.jasonxqh.middleware.sdk.infrastructure.chatbot.IOpenAI;
import io.github.jasonxqh.middleware.sdk.infrastructure.chatbot.dto.ChatCompletionRequestDTO;
import io.github.jasonxqh.middleware.sdk.infrastructure.chatbot.dto.ChatCompletionSyncResponseDTO;
import io.github.jasonxqh.middleware.sdk.types.utils.BearerTokenUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * @author : jasonxu
 * @mailto : xuqihang74@gmail.com
 * @created : 2024/10/29, 星期二
 **/
public class ChatGLM implements IOpenAI {
    private final String apiHost;
    private final String apiKeySecret;

    public ChatGLM(String apiHost, String apiKeySecret) {
        this.apiHost = apiHost;
        this.apiKeySecret = apiKeySecret;
    }


    @Override
    public ChatCompletionSyncResponseDTO completions(ChatCompletionRequestDTO request) throws Exception {
        System.out.println("apiHost: "+apiHost);
        String token = BearerTokenUtils.getToken(apiKeySecret);

        URL url = new URL(apiHost);

        System.out.println("token: "+token);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Authorization", "Bearer " + token);
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        urlConnection.setDoOutput(true);
        try (OutputStream os = urlConnection.getOutputStream()) {
            byte[] input = JSON.toJSONString(request).getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
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


        return  JSON.parseObject(response.toString(), ChatCompletionSyncResponseDTO.class);
    }

}
