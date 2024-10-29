package io.github.jasonxqh.middleware.sdk.infrastructure.chatbot;

import io.github.jasonxqh.middleware.sdk.infrastructure.chatbot.dto.ChatCompletionRequestDTO;
import io.github.jasonxqh.middleware.sdk.infrastructure.chatbot.dto.ChatCompletionSyncResponseDTO;

/**
 * @author : jasonxu
 * @mailto : xuqihang74@gmail.com
 * @created : 2024/10/29, 星期二
 **/
public interface IOpenAI {
    ChatCompletionSyncResponseDTO completions(ChatCompletionRequestDTO request)throws Exception;

}
