package com.huashan.smart.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * RTF Prompt 框架演示控制器
 * 使用 AiConfig 中定义的 RTF Prompt Bean
 */
@RestController
@RequestMapping("/rtf")
public class RtfPromptController {

    private final ChatClient chatClient;
    private final String dormitoryPrompt;

    public RtfPromptController(ChatClient.Builder chatClientBuilder,
                               @Qualifier("dormitoryPrompt") String dormitoryPrompt) {
        this.chatClient = chatClientBuilder.build();
        this.dormitoryPrompt = dormitoryPrompt;
    }

    /**
     * 使用RTF框架Prompt的AI对话
     * GET /rtf/chat?message=我的宿舍水龙头坏了
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
                .system(dormitoryPrompt)
                .user(message)
                .call()
                .content();
    }
}
