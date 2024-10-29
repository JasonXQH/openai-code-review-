package io.github.jasonxqh.middleware.sdk.infrastructure.weixin;

import com.alibaba.fastjson2.JSON;
import io.github.jasonxqh.middleware.sdk.infrastructure.weixin.dto.TemplateMessageDTO;
import io.github.jasonxqh.middleware.sdk.types.utils.WXAccessTokenUtils;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @author : jasonxu
 * @mailto : xuqihang74@gmail.com
 * @created : 2024/10/29, 星期二
 **/
public class WeiXin {
    private final Logger logger = (Logger) LoggerFactory.getLogger(WeiXin.class);

    private final String appid;
    private final String secret;
    private final String touser;
    private final String template_id;

    public WeiXin(String appid, String secret, String touser, String template_id) {
        this.appid = appid;
        this.secret = secret;
        this.touser = touser;
        this.template_id = template_id;
    }

    public void sendTemplateMessage(String logUrl, Map<String,Map<String,String> > data){
        String accessToken = WXAccessTokenUtils.getAccessToken(appid, secret);
        TemplateMessageDTO templateMessageDTO = new TemplateMessageDTO(touser, template_id);
        templateMessageDTO.setUrl(logUrl);
        templateMessageDTO.setData(data);
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+accessToken;
        sendPostRequest(url, JSON.toJSONString(templateMessageDTO));
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
