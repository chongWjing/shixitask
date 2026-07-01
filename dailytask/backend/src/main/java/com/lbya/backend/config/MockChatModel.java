package com.lbya.backend.config;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.Collections;

public class MockChatModel implements ChatModel {
    @Override
    public ChatResponse call(Prompt prompt) {
        String msg = "【Mock模式】AI服务未配置，请设置DEEPSEEK_API_KEY环境变量后重启。";
        return new ChatResponse(Collections.singletonList(new Generation(new AssistantMessage(msg))));
    }
}
