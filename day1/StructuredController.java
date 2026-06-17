package com.huashan.springaidemo.controller;

import com.huashan.springaidemo.model.StudentInfo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/struct")
public class StructuredController {
    private final ChatClient chatClient;

    public StructuredController(ChatClient.Builder chatClientBuilder){
        this.chatClient=chatClientBuilder.build();
    }


    @GetMapping("/student")
    public StudentInfo extractStudent(@RequestParam String desc){
        return chatClient.prompt()
                .system("从用户的描述中提取学生信息，并按照JSON格式返回")
                .user(desc)
                .call()
                .entity(StudentInfo.class);

    }

}
