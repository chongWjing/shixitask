package com.lbya.backend.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("mock")
public class AiMockConfig {
    @Bean
    public ChatModel chatModel() {
        return new MockChatModel();
    }
}
