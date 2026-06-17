package com.lbya.smart.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 多角色对话控制器
 * 演示不同 ChatClient Bean 的不同角色设定
 */
@RestController
@RequestMapping("/role")
public class RoleChatController {

    private final ChatClient dormitoryClient;
    private final ChatClient tutorClient;

    public RoleChatController(
            @Qualifier("dormitoryChatClient") ChatClient dormitoryClient,
            @Qualifier("tutorChatClient") ChatClient tutorClient) {
        this.dormitoryClient = dormitoryClient;
        this.tutorClient = tutorClient;
    }

    /**
     * 宿舍助手对话
     * GET /role/dormitory?message=301室有多少人
     */
    @GetMapping("/dormitory")
    public String dormitoryChat(@RequestParam String message) {
        return dormitoryClient.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * 编程老师对话
     * GET /role/tutor?message=什么是Spring Boot
     */
    @GetMapping("/tutor")
    public String tutorChat(@RequestParam String message) {
        return tutorClient.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * 同一问题，不同角色对比
     * GET /role/compare?message=帮我查成绩
     */
    @GetMapping("/compare")
    public Map<String, String> compare(@RequestParam String message) {
        String dormReply = dormitoryClient.prompt()
                .user(message)
                .call()
                .content();

        String tutorReply = tutorClient.prompt()
                .user(message)
                .call()
                .content();

        return Map.of(
                "宿舍助手小宿", dormReply,
                "编程老师王老师", tutorReply
        );
    }
}
