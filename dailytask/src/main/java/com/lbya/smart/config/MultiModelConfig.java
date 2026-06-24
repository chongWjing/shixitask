package com.lbya.smart.config;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 多模型配置
 * - deepseekV3ChatModel（@Primary）：快速模型，适合日常对话
 * - deepseekR1ChatModel：推理模型，适合复杂推理
 */
@Configuration
public class MultiModelConfig {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    /**
     * DeepSeek V3（快速模型，@Primary 默认）
     */
    @Bean("deepseekV3ChatModel")
    @Primary
    public OpenAiChatModel deepseekV3ChatModel() {
        OpenAiApi api = OpenAiApi.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model("deepseek-v4-flash")
                .temperature(0.7)
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(options)
                .build();
    }

    /**
     * DeepSeek R1（推理模型，需通过 @Qualifier 注入）
     */
    @Bean("deepseekR1ChatModel")
    public OpenAiChatModel deepseekR1ChatModel() {
        OpenAiApi api = OpenAiApi.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .build();

        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model("deepseek-reasoner")
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(options)
                .build();
    }
}
