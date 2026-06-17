package com.lbya.smart.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SseEmitter方式实现流式对话（对比演示）
 * 实际项目中推荐用Flux方式
 */
@Slf4j
@RestController
@RequestMapping("/sse-emitter")
public class SseEmitterChatController {

    private final ChatClient chatClient;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public SseEmitterChatController(@Qualifier("dormitoryChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * SseEmitter方式流式对话
     * GET /sse-emitter/chat?message=你好
     */
    @GetMapping(value = "/chat", produces = "text/event-stream;charset=UTF-8")
    public SseEmitter chatStream(@RequestParam String message) {
        SseEmitter emitter = new SseEmitter(60000L);

        executorService.execute(() -> {
            try {
                Flux<String> flux = chatClient.prompt()
                        .user(message)
                        .stream()
                        .content();

                flux.doOnNext(chunk -> {
                            try {
                                emitter.send(SseEmitter.event().data(chunk));
                            } catch (IOException e) {
                                log.error("SSE发送chunk失败：", e);
                            }
                        })
                        .doOnComplete(() -> {
                            try {
                                emitter.send(SseEmitter.event().data("[DONE]"));
                                emitter.complete();
                            } catch (IOException e) {
                                log.error("SSE完成信号发送失败：", e);
                            }
                        })
                        .doOnError(emitter::completeWithError)
                        .subscribe();
            } catch (Exception e) {
                log.error("SseEmitter流式对话异常：", e);
                try {
                    emitter.send(SseEmitter.event().data("AI服务异常"));
                    emitter.complete();
                } catch (IOException ex) {
                    log.error("SSE异常通知发送失败：", ex);
                }
            }
        });

        return emitter;
    }
}
