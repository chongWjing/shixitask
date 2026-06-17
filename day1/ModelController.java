package com.huashan.springaidemo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mode")
public class ModelController {
    private  final ChatClient chatClient;

    public  ModelController(ChatClient.Builder chatClientBuilder){
        this.chatClient=chatClientBuilder.build();
    }

    @GetMapping("/system")
    public String withSystemPrompt(@RequestParam String question){
        return chatClient.prompt()
                .system("你是高校宿舍助手，回答简介，不要超过50字")
                .user(question)
                .call()
                .content();
    }

    //第四种

    @GetMapping("/template")
    public String withTemplate(@RequestParam String building,@RequestParam String student){
        return chatClient.prompt()
                .system("你是一个宿舍管理助手，回答简洁。")
                .user(u->u.text("帮我查询{building}楼的{student}的考勤记录，并给出分析")
                        .param("building",building)
                        .param("student",student))
                .call()
                .content();
    }

}
