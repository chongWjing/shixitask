package com.huashan.springaidemo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
public class SimpleChatController {

    private final ChatClient chatClient;

    public SimpleChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient=chatClientBuilder.build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message){
        return chatClient.prompt().user( message).call().content();
    }

    //带有Toen的对话，token  1个中文 1-3token   1个英文单词 = 1token
    @GetMapping("/chat/info")
    public Map<String,Object> chatinfo(@RequestParam String message){
        ChatResponse chatResponse = chatClient.prompt().user( message).call().chatResponse();
        String content= Optional.ofNullable(chatResponse.getResult())
                .map(result->result.getOutput())
                .map(AssistantMessage::getText)
                .orElse("无响应内容");
        var usage=chatResponse.getMetadata().getUsage();

        return Map.of(
                "content",content,
                "promptTokens",usage.getPromptTokens(),
                "completionTokens",usage.getCompletionTokens(),
                "totalTokens",usage.getTotalTokens());
    }


    //错误处理
    @GetMapping("/chat/safe")
    public Map<String,Object> chatSafe(@RequestParam String message){
        try{
            String reply=chatClient.prompt()
                    .user( message)
                    .call()
                    .content();
            //如果成功了
            return Map.of("success",true,"reply",reply);
        }catch (Exception e){
            return Map.of("success",false,"error",e.getMessage());
        }
    }

    //覆盖模型参数，进行对话
    @GetMapping("/chat/strict")
    public String chatStrict(@RequestParam String message){
        return chatClient.prompt()
                .user(message)
                .options(ChatOptions.builder()
                        .temperature(0.1)
                        .maxTokens(500)
                        .build())
                .call()
                .content();
    }

}
