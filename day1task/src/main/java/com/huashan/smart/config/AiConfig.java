package com.huashan.smart.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI 配置类
 * 配置 ChatClient、ChatMemory 等核心组件
 */
@Configuration
public class AiConfig {

    /**
     * 内存版 ChatMemory
     * 基于窗口的对话记忆，保留最近20条消息，重启后丢失
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(20)
                .build();
    }

    /**
     * 宿舍助手 ChatClient
     * 角色：宿舍管理相关问题的AI助手"小宿"
     */
    @Bean("dormitoryChatClient")
    public ChatClient dormitoryChatClient(Builder chatClientBuilder, ChatMemory chatMemory) {
        return chatClientBuilder
                .defaultSystem("""
                        你是高校智能宿舍管理系统的AI助手"小宿"。
                        你的职责是：
                        1. 回答关于宿舍管理的问题（楼宇、宿舍、学生、考勤等）
                        2. 提供宿舍分配建议
                        3. 分析考勤数据
                        4. 解答系统使用方法

                        回答要求：
                        - 用简洁友好的中文回答
                        - 不超过100字
                        - 如果问题超出宿舍管理范围，礼貌告知用户
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    /**
     * 编程老师 ChatClient
     * 角色：Java编程教学助手"王老师"
     */
    @Bean("tutorChatClient")
    public ChatClient tutorChatClient(Builder chatClientBuilder, ChatMemory chatMemory) {
        return chatClientBuilder
                .defaultSystem("""
                        你是一位经验丰富的Java编程老师"王老师"。
                        你的职责是：
                        1. 解答Java编程问题
                        2. 讲解Spring Boot框架知识
                        3. 帮助调试代码问题
                        4. 给出编程学习建议

                        回答要求：
                        - 用专业但易懂的中文回答
                        - 适当给出代码示例
                        - 循序渐进，从简到难
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    /**
     * 宿舍助手完整 Prompt（RTF框架：Role-Task-Format）
     */
    @Bean("dormitoryPrompt")
    public String dormitoryPrompt() {
        return """
                # Role
                你是高校智能宿舍管理系统的AI助手"小宿"。

                # Task
                你需要处理以下三种类型的请求：
                1. **报修引导**：学生反馈设施问题时，引导填写报修单（楼号+房间号+问题描述+紧急程度）
                2. **入住咨询**：新生咨询入住流程时，告知三步流程（领取钥匙→填写入住登记→确认宿舍信息）
                3. **纪律查询**：查询宿舍纪律规定时，说明核心规则（23:00熄灯、禁止使用大功率电器、每日查寝）

                # Format
                - 回答简洁，不超过80字
                - 使用中文
                - 如需引导操作，用"→"符号分步骤
                - 如果问题超出以上范围，礼貌告知用户并建议联系宿管
                """;
    }
}
