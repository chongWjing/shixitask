package com.lbya.smart.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 五种同步调用模式演示控制器
 *
 * 模式二（System Prompt）和模式四（参数化模板）
 */
@RestController
@RequestMapping("/mode")
public class ModeController {

    private final ChatClient chatClient;

    public ModeController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    // ==================== 模式二：System Prompt ====================

    /**
     * 【模式二】每次调用时临时指定 System Prompt
     * GET /mode/system?question=宿舍几点熄灯
     */
    @GetMapping("/system")
    public String withSystemPrompt(@RequestParam String question) {
        return chatClient.prompt()
                .system("你是高校宿舍管理助手，回答简洁，不超过50字。")
                .user(question)
                .call()
                .content();
    }

    // ==================== 模式四：参数化Prompt模板 ====================

    /**
     * 【模式四】参数化 Prompt 模板
     * GET /mode/template?building=3&student=张三
     */
    @GetMapping("/template")
    public String withTemplate(@RequestParam String building,
                               @RequestParam String student) {
        return chatClient.prompt()
                .system("你是宿舍管理助手，回答简洁。")
                .user(u -> u.text("帮我查{building}楼的{student}的考勤记录，并给出分析")
                        .param("building", building)
                        .param("student", student))
                .call()
                .content();
    }
}
