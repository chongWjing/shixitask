package com.huashan.smart.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 多轮对话演示控制器
 * 演示 ChatMemory 自动维护上下文
 */
@RestController
@RequestMapping("/memory")
public class MemoryChatController {

    private final ChatClient chatClient;

    public MemoryChatController(@Qualifier("dormitoryChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 多轮对话接口
     * GET /memory/chat?conversationId=user001&message=我叫张三住301
     * GET /memory/chat?conversationId=user001&message=我住哪
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String conversationId,
                       @RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param("chat_memory_conversation_id", conversationId))
                .call()
                .content();
    }
}
