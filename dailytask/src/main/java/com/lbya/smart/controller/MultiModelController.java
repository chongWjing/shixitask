package com.lbya.smart.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 多模型对话控制器 + 智能路由
 * - V3（deepseek-chat）：快速，适合日常对话
 * - R1（deepseek-reasoner）：慢但准，适合复杂推理
 * - 智能路由：关键词匹配自动选模型
 */
@RestController
@RequestMapping("/model")
public class MultiModelController {

    private final ChatClient v3Client;
    private final ChatClient r1Client;

    public MultiModelController(
            @Qualifier("deepseekV3ChatModel") ChatModel v3Model,
            @Qualifier("deepseekR1ChatModel") ChatModel r1Model) {
        this.v3Client = ChatClient.builder(v3Model).build();
        this.r1Client = ChatClient.builder(r1Model).build();
    }

    /** V3 快速模型对话 GET /model/v3?message=你好 */
    @GetMapping("/v3")
    public String chatWithV3(@RequestParam String message) {
        return v3Client.prompt()
                .user(message)
                .call()
                .content();
    }

    /** R1 推理模型对话 GET /model/r1?message=请推导勾股定理 */
    @GetMapping("/r1")
    public String chatWithR1(@RequestParam String message) {
        return r1Client.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * 智能路由：关键词匹配自动选模型
     * GET /model/smart?message=请分析为什么...
     */
    @GetMapping("/smart")
    public String smartChat(@RequestParam String message) {
        if (needsReasoning(message)) {
            return r1Client.prompt()
                    .user(message)
                    .call()
                    .content();
        } else {
            return v3Client.prompt()
                    .user(message)
                    .call()
                    .content();
        }
    }

    private boolean needsReasoning(String message) {
        String[] reasoningKeywords = {
                "推导", "证明", "分析", "解释原因",
                "为什么", "原理", "逻辑", "深入",
                "比较", "评估", "论证", "推理"
        };
        for (String keyword : reasoningKeywords) {
            if (message.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
