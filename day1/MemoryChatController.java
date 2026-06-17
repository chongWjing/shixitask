package com.huashan.springaidemo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/memory")
public class MemoryChatController {

    private  final ChatClient chatClient;

    public MemoryChatController(@Qualifier("dormitoryChatClient") ChatClient chatClient){
        this.chatClient=chatClient;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String conversationId,@RequestParam String message){
        return chatClient.prompt()
                .user(message)
                .advisors(advisor->advisor.param("chat_memory_conversation_id",conversationId))
                .call()
                .content();
    }
}
