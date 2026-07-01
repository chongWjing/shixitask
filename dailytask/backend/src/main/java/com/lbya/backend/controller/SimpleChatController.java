package com.lbya.backend.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

/**
 * 简单AI对话控制器
 * 演示Spring AI ChatClient最基本的用法
 *
 * 4个接口：
 * 1. /chat       - 最简调用
 * 2. /chat/info  - 带Token统计
 * 3. /chat/safe  - 带错误处理
 * 4. /chat/strict- 覆盖模型参数
 */
@RestController
public class SimpleChatController {

    private final ChatClient chatClient;

    public SimpleChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 【接口1】最简AI对话接口
     * GET /chat?message=你好
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * 【接口2】带Token统计的AI对话接口
     * GET /chat/info?message=你好
     */
    @GetMapping("/chat/info")
    public Map<String, Object> chatInfo(@RequestParam String message) {
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .call()
                .chatResponse();

        String content = Optional.ofNullable(chatResponse.getResult())
                .map(result -> result.getOutput())
                .map(AssistantMessage::getText)
                .orElse("无响应内容");

        var usage = chatResponse.getMetadata().getUsage();

        return Map.of(
                "content", content,
                "promptTokens", usage.getPromptTokens(),
                "completionTokens", usage.getCompletionTokens(),
                "totalTokens", usage.getTotalTokens()
        );
    }

    /**
     * 【接口3】带错误处理的AI对话接口
     * GET /chat/safe?message=你好
     */
    @GetMapping("/chat/safe")
    public Map<String, Object> chatSafe(@RequestParam String message) {
        try {
            String reply = chatClient.prompt()
                    .user(message)
                    .call()
                    .content();
            return Map.of("success", true, "reply", reply);
        } catch (Exception e) {
            return Map.of("success", false, "error", "AI服务暂时不可用：" + e.getMessage());
        }
    }

    /**
     * 【接口4】覆盖模型参数的AI对话接口
     * GET /chat/strict?message=写一首诗
     */
    @GetMapping("/chat/strict")
    public String chatStrict(@RequestParam String message) {
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
