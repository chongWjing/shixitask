package com.lbya.backend.controller;

import com.lbya.backend.util.Result;
import com.lbya.backend.service.AiChatService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * AI聊天控制器（正式接口）
 * 采用分层架构：Controller → Service → ChatClient
 * 使用统一的 Result 响应格式
 *
 * 两个接口：
 * - POST /api/aichat         → 同步对话
 * - GET  /api/aichat/stream  → 流式SSE对话
 */
@RestController
@RequestMapping("/api/aichat")
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    /**
     * 同步对话
     * POST /api/aichat
     */
    @PostMapping
    public Result<String> chat(@RequestBody ChatRequest chatRequest) {
        String reply = aiChatService.chat(
                chatRequest.getConversationId(),
                chatRequest.getMessage()
        );
        return Result.success(reply);
    }

    /**
     * 流式对话（SSE）
     * GET /api/aichat/stream?conversationId=user001&message=你好
     */
    @GetMapping(value = "/stream", produces = "text/event-stream;charset=UTF-8")
    public Flux<String> chatStream(@RequestParam String conversationId,
                                   @RequestParam String message) {
        return aiChatService.chatStream(conversationId, message);
    }

    /**
     * 聊天请求体
     */
    @Data
    public static class ChatRequest {
        /** 会话ID（不传则默认为 "default"） */
        private String conversationId = "default";
        /** 用户消息（必填） */
        private String message;
    }
}
