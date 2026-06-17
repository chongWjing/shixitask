package com.lbya.smart.service.impl;

import com.lbya.smart.service.AiChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * AI聊天Service实现类
 * 注入 dormitoryChatClient（自带角色+记忆）
 */
@Slf4j
@Service
public class AiChatServiceImpl implements AiChatService {

    private final ChatClient chatClient;

    public AiChatServiceImpl(@Qualifier("dormitoryChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 同步对话实现
     */
    @Override
    public String chat(String conversationId, String message) {
        try {
            return chatClient.prompt()
                    .user(message)
                    .advisors(advisor -> advisor
                            .param("chat_memory_conversation_id", conversationId))
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("AI同步对话异常：", e);
            return "AI服务暂时不可用，请稍后再试。";
        }
    }

    /**
     * 流式对话实现
     */
    @Override
    public Flux<String> chatStream(String conversationId, String message) {
        try {
            return chatClient.prompt()
                    .user(message)
                    .advisors(advisor -> advisor
                            .param("chat_memory_conversation_id", conversationId))
                    .stream()
                    .content();
        } catch (Exception e) {
            log.error("AI流式对话异常：", e);
            return Flux.just("AI服务暂时不可用，请稍后再试。");
        }
    }
}
