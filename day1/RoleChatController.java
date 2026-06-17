package com.huashan.springaidemo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/role")
public class RoleChatController{
    private final ChatClient dormitoryClient;
    private final ChatClient tutorClient;

    public RoleChatController(ChatClient.Builder chatClientBuilder,
                              @Qualifier("dormitoryChatClient") ChatClient dormitoryClient,
                              @Qualifier("tutorChatClient") ChatClient tutorClient){
        this.dormitoryClient=dormitoryClient;
        this.tutorClient=tutorClient;
    }

    @GetMapping("/tutor")
    public String tutorChat(@RequestParam String message){
        return tutorClient.prompt()
                .user(message)
                .call()
                .content();
    }
}

