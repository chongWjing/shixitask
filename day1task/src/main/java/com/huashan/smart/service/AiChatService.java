package com.huashan.smart.service;

import reactor.core.publisher.Flux;

/**
 * AI聊天Service接口
 * 提供同步对话和流式对话能力
 */
public interface AiChatService {

    /**
     * 同步对话
     * @param conversationId 会话ID（用于多轮对话隔离）
     * @param message 用户消息
     * @return AI回复内容
     */
    String chat(String conversationId, String message);

    /**
     * 流式对话（Flux）
     * @param conversationId 会话ID
     * @param message 用户消息
     * @return 流式响应
     */
    Flux<String> chatStream(String conversationId, String message);
}
