package com.lbya.smart.controller;

import com.lbya.smart.model.StudentInfo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 结构化输出演示控制器
 * AI回复自动映射为Java对象
 */
@RestController
@RequestMapping("/struct")
public class StructuredController {

    private final ChatClient chatClient;

    public StructuredController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 结构化输出：AI回复自动转为StudentInfo对象
     * GET /struct/student?desc=张三住3号楼301，手机13800138000，本月全勤
     */
    @GetMapping("/student")
    public StudentInfo extractStudent(@RequestParam String desc) {
        return chatClient.prompt()
                .system("从用户描述中提取学生信息，按JSON格式返回，字段：name/building/room/phone/attendanceStatus")
                .user(desc)
                .call()
                .entity(StudentInfo.class);
    }
}
