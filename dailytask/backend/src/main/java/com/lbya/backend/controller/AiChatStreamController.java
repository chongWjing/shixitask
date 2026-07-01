package com.lbya.backend.controller;

import com.lbya.backend.service.AiChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * 流式AI对话控制器（高级版）
 * 自定义SSE事件类型：content / done / error
 *
 * 前端监听方式：
 * eventSource.addEventListener("content", (e) => { 追加文本 })
 * eventSource.addEventListener("done",    (e) => { 关闭连接 })
 * eventSource.addEventListener("error",   (e) => { 显示错误 })
 */
@Slf4j
@RestController
@RequestMapping("/api/aichat")
public class AiChatStreamController {

    private final AiChatService aiChatService;

    public AiChatStreamController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    /**
     * 高级流式对话（带自定义SSE事件类型 + ChatMemory）
     * GET /api/aichat/stream-v2?conversationId=user001&message=你好
     */
    @GetMapping(value = "/stream-v2", produces = "text/event-stream;charset=UTF-8")
    public Flux<ServerSentEvent<String>> chatStreamV2(
            @RequestParam String conversationId,
            @RequestParam String message) {

        return aiChatService.chatStream(conversationId, message)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .event("content")
                        .data(chunk)
                        .build())
                .concatWith(Flux.just(
                        ServerSentEvent.<String>builder()
                                .event("done")
                                .data("[DONE]")
                                .build()))
                .onErrorResume(e -> {
                    log.error("流式对话异常：", e);
                    return Flux.just(
                            ServerSentEvent.<String>builder()
                                    .event("error")
                                    .data("AI服务暂时不可用")
                                    .build());
                });
    }
}
