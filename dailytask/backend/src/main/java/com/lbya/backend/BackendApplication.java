package com.lbya.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.ai.model.openai.autoconfigure.OpenAiAudioSpeechAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {
    OpenAiAudioSpeechAutoConfiguration.class
})
@MapperScan("com.lbya.backend.mapper")
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner printUrls() {
        return args -> {
            System.out.println();
            System.out.println("╔══════════════════════════════════════════════════════╗");
            System.out.println("║          🌸 花之恋鲜花电商平台 启动成功！              ║");
            System.out.println("╠══════════════════════════════════════════════════════╣");
            System.out.println("║                                                      ║");
            System.out.println("║  🛒 商城前台：  http://localhost:5173/#/shop           ║");
            System.out.println("║  👤 用户登录：  http://localhost:5173/#/shop/login     ║");
            System.out.println("║  🔧 管理后台：  http://localhost:5173/#/admin/login    ║");
            System.out.println("║                                                      ║");
            System.out.println("║  📋 测试账号：                                          ║");
            System.out.println("║     管理员：admin / 123456                             ║");
            System.out.println("║     普通用户：test / 123456                            ║");
            System.out.println("║                                                      ║");
            System.out.println("╚══════════════════════════════════════════════════════╝");
            System.out.println();
        };
    }

}
