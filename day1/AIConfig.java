package com.huashan.springaidemo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;

@Configuration
public class AIConfig {

    @Bean
    public ChatMemory chatMemory(){
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(20)
                .build();
    }

    @Bean("dormitoryChatClient")
    public ChatClient dormitoryChatClient(Builder chatClientBuilder, ChatMemory chatMemory){
        return chatClientBuilder
                .defaultSystem("""
                        你是一个高校智能宿舍管理系统的AI助手“小宿”。
                        你的职责：
                        1 回答关于宿舍管理的问题（楼宇，宿舍，学生，考勤等）
                        2 提供宿舍分配建议
                        3 分析考勤数据
                        4 解答系统使用方法
                        回答要求：
                        - 使用简洁友好的中文回答
                        - 不要超过100个字
                        - 如果问题超过了宿舍管理范围，要礼貌告知
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }


    //编程助手
    @Bean("tutorChatClient")
    public ChatClient tutorChatClent(Builder chatClientBuilder,ChatMemory chatMemory){
        return chatClientBuilder
                .defaultSystem("""
                        你是一个丰富的Java编程老师“王老师”.
                        你的职责：
                        1 解答Java的编程问题
                        2 讲解Spring boot 的知识
                        3 帮助调试代码
                        4 给出编程的学习建议
                        回答要求：
                        - 使用专业易懂的中文进行回答
                        - 适当的给出代码示例
                        - 循序渐进，从简单到复杂
                        
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @Bean("dormitoryPrompt")
    public String dormitoryPrompt(){
        return """
                #Role
                你是高校智能宿舍管理系统的AI助手"小宿"
                #Task
                你需要处理以下三类请求：
                1 “报修引导”:学生反应设施问题时，引导她填写报修单
                2 “入住咨询”:新生咨询入住流程时，告诉他三步流程
                3 "纪律查询"： 查询宿舍纪律规定，详细说明核心规则
                # format
                - 回答简洁，不要超过50字
                - 使用中文
                """;
    }
}
