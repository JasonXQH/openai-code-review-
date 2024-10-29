package io.github.jasonxqh.middleware.sdk.infrastructure.chatbot.dto;
import java.util.List;



public class ChatCompletionSyncResponseDTO {

    List<Choices> choices;

    int created;

    String id;

    String model;

    String requestId;

    Usage usage;


    public void setChoices(List<Choices> choices) {
        this.choices = choices;
    }
    public List<Choices> getChoices() {
        return choices;
    }

    public void setCreated(int created) {
        this.created = created;
    }
    public int getCreated() {
        return created;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setModel(String model) {
        this.model = model;
    }
    public String getModel() {
        return model;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    public String getRequestId() {
        return requestId;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }
    public Usage getUsage() {
        return usage;
    }


    public class Choices {

        String finishReason;

        int index;

        Message message;


        public void setFinishReason(String finishReason) {
            this.finishReason = finishReason;
        }
        public String getFinishReason() {
            return finishReason;
        }

        public void setIndex(int index) {
            this.index = index;
        }
        public int getIndex() {
            return index;
        }

        public void setMessage(Message message) {
            this.message = message;
        }
        public Message getMessage() {
            return message;
        }

    }

    public class Message {

        String content;
        String role;


        public void setContent(String content) {
            this.content = content;
        }
        public String getContent() {
            return content;
        }

        public void setRole(String role) {
            this.role = role;
        }
        public String getRole() {
            return role;
        }

    }


    public class Usage {

        int completionTokens;

        int promptTokens;

        int totalTokens;


        public void setCompletionTokens(int completionTokens) {
            this.completionTokens = completionTokens;
        }
        public int getCompletionTokens() {
            return completionTokens;
        }

        public void setPromptTokens(int promptTokens) {
            this.promptTokens = promptTokens;
        }
        public int getPromptTokens() {
            return promptTokens;
        }

        public void setTotalTokens(int totalTokens) {
            this.totalTokens = totalTokens;
        }
        public int getTotalTokens() {
            return totalTokens;
        }

    }
}