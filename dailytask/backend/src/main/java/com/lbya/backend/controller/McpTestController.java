package com.lbya.backend.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * MCP 工具调用测试控制器
 *
 * AI 根据 @Tool 的 description 自动选择并调用合适的工具，无需 if-else 判断
 */
@RestController
@RequestMapping("/mcp")
public class McpTestController {

    private final ChatClient chatClient;

    public McpTestController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * MCP 工具调用测试
     * GET /mcp/chat?message=123加456等于多少
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .toolNames("add", "subtract", "multiply", "divide",
                           "average", "calculateDormitoryCapacity",
                           "calculateEmptyBeds", "sendCheckInEmail")
                .call()
                .content();
    }
}
