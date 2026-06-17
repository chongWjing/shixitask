# Day1：Spring AI 项目搭建 + 同步调用全实操

> **日期**：6月16日 9:00-12:00
> **时长**：3小时（课间休息10分钟）
> **目标**：从零创建 Spring Boot 项目，完成 Spring AI 五种同步调用模式，实现完整后端 AI 服务
> **版本**：Spring Boot 3.5.15 + Spring AI 1.0.0 GA + JDK 17 + DeepSeek API

---

## Part 1：项目搭建 + 第一个 AI 接口（9:00-9:40）

### 步骤1：创建 Spring Boot 3.5 项目

1. 打开 [Spring Initializr](https://start.spring.io/)
2. 配置如下：
   - **Project**: Maven
   - **Language**: Java
   - **Spring Boot**: 3.5.15
   - **Group**: com.huashan
   - **Artifact**: smart
   - **Name**: smart
   - **Package name**: com.huashan.smart
   - **Packaging**: Jar
   - **Java**: 17
3. 添加依赖：**Spring Web**
4. 点击 **GENERATE** 下载并解压到工作目录
5. 用 IDEA 打开项目，等待 Maven 索引完成

> **💡 为什么要用 Spring Boot 3.5？** Spring AI 1.0.0 GA 要求 Spring Boot 3.x 以上版本，而 3.5.x 是目前最新的稳定版本，对 Spring AI 提供了最好的兼容性支持。Spring Boot 3.x 底层基于 Java 17+，所以 JDK 版本必须 ≥ 17。

---

### 步骤2：pom.xml 添加 Spring AI 依赖

打开 `pom.xml`，在 `<dependencies>` 中添加 Spring AI 依赖，在 `<properties>` 中添加版本号：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- Spring Boot 父工程：统一管理所有 Spring Boot 相关依赖的版本号 -->
    <!-- 不需要手动指定子依赖版本，父工程已经帮你管理好了 -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.15</version>
        <relativePath/> <!-- 从Maven仓库查找，不从本地路径查找 -->
    </parent>

    <!-- 我们项目的坐标：com.huashan:smart -->
    <groupId>com.huashan</groupId>
    <artifactId>smart</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>smart</name>
    <description>smart</description>

    <properties>
        <java.version>17</java.version>
        <!-- Spring AI 版本号单独管理，方便后续升级时只改这一处 -->
        <spring-ai.version>1.0.0</spring-ai.version>
    </properties>

    <dependencies>
        <!-- SpringBoot Web：提供 REST 接口能力（@RestController、@GetMapping 等） -->
        <!-- 这是项目的 Web 层基础，AI 接口也需要通过 HTTP 暴露给前端 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring AI - OpenAI 兼容模型（用于连接 DeepSeek） -->
        <!-- 核心依赖！这个 starter 做了三件事：
             1. 自动配置 ChatClient.Builder Bean（你不需要手动创建）
             2. 自动配置 OpenAI ChatModel（根据 application.yaml 中的配置）
             3. 提供ChatClient、ChatMemory、Advisor 等所有 Spring AI API -->
        <!-- ⚠️ 注意：依赖名是 spring-ai-starter-model-openai（GA版）
             不是旧版的 spring-ai-openai-spring-boot-starter -->
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-starter-model-openai</artifactId>
            <version>${spring-ai.version}</version>
        </dependency>

        <!-- Lombok：自动生成 getter/setter/toString/构造器等样板代码 -->
        <!-- 用 @Data 注解即可自动生成所有 getter/setter，不用手写 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- SpringBoot Test：单元测试框架 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- Spring Boot Maven 插件：打包为可执行 jar -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <!-- Lombok 只在编译时使用，不需要打包到最终 jar 中 -->
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

> ⚠️ **版本要点**：
> - Spring AI 1.0.0 GA 已进入 Maven 中央仓库，**不需要** `spring-milestones` 仓库
> - 依赖名是 `spring-ai-starter-model-openai`，不是旧版的 `spring-ai-openai-spring-boot-starter`
> - 这里直接在依赖中指定 `${spring-ai.version}`，也可以通过 BOM 统一管理

> **💡 为什么用 `spring-ai-starter-model-openai` 连接 DeepSeek？** 因为 DeepSeek 的 API 接口兼容 OpenAI 的协议格式。Spring AI 的 OpenAI 模块本质上是一个"OpenAI 协议兼容客户端"，只要改 `base-url` 就能对接任何兼容该协议的模型服务。这就像 JDBC 统一了数据库访问一样——你只需要学一套 API，就能对接不同的 AI 模型。

---

### 步骤3：配置 application.yaml 连接 DeepSeek API

删除 `src/main/resources/application.properties`，创建 `application.yaml`：

```yaml
server:
  port: 8080  # 服务端口，默认8080

spring:
  ai:
    openai:
      api-key: ${DEEPSEEK_API_KEY:sk-your-api-key-here}
      base-url: https://api.deepseek.com
      chat:
        options:
          model: deepseek-chat
          temperature: 0.7
```

> ⚠️ **配置要点**：
> - `base-url` 改为 DeepSeek 的地址，因为 DeepSeek 兼容 OpenAI 接口
> - `api-key` 填你自己的 DeepSeek API Key（去 [platform.deepseek.com](https://platform.deepseek.com) 申请）
> - 也可以用环境变量 `DEEPSEEK_API_KEY` 传入，避免硬编码
> - `model` 填 `deepseek-chat`（V3模型），不要填 `gpt-3.5-turbo`

> **💡 如果要切换到其他模型怎么办？** 只需要改 `base-url` 和 `model` 两个配置项，代码完全不用动。例如：
> - 切换到 OpenAI：`base-url: https://api.openai.com`，`model: gpt-4o-mini`
> - 切换到本地 Ollama：`base-url: http://localhost:11434`，`model: qwen2.5:7b`
> 这就是 Spring AI 统一抽象层的核心价值——一次开发，处处运行。

---

### 步骤4：创建第一个 ChatClient 接口

> **💡 什么是 ChatClient？** ChatClient 是 Spring AI 提供的高级 API，类似于 JdbcTemplate 之于 JDBC。它封装了与 AI 模型交互的所有细节，你只需要关注"发什么消息"和"怎么处理回复"。
>
> **ChatClient 的链式调用流程**：
> ```
> chatClient.prompt()  →  开始构建提示词
>     .user("你好")    →  设置用户消息
>     .system("角色")  →  设置系统角色（可选）
>     .call()          →  同步调用（.stream() 是流式调用）
>     .content()       →  提取回复文本
> ```
>
> **类比理解**：就像 HttpClient 发送 HTTP 请求一样——`prompt()` 相当于创建请求，`.user()` 相当于设置请求体，`.call()` 相当于发送请求，`.content()` 相当于读取响应体。

创建包 `com.huashan.smart.controller`，新建 `SimpleChatController.java`：

```java
package com.huashan.smart.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

/**
 * 简单AI对话控制器
 * 演示Spring AI ChatClient最基本的用法
 *
 * 本Controller包含4个接口，从简单到复杂：
 * 1. /chat       - 最简调用，直接返回AI回复文本
 * 2. /chat/info  - 带Token统计，查看每次调用的Token消耗
 * 3. /chat/safe  - 带错误处理，防止AI服务异常时接口崩溃
 * 4. /chat/strict - 覆盖模型参数，控制AI回复风格
 */
@RestController  // 标记为REST控制器，返回值自动序列化为JSON
public class SimpleChatController {

    // ChatClient：Spring AI 的核心API，用于与AI模型交互
    private final ChatClient chatClient;

    /**
     * 构造器注入 ChatClient.Builder
     *
     * 为什么注入的是 Builder 而不是 ChatClient？
     * - ChatClient.Builder 由 Spring AI 自动配置（Auto-Configuration）创建
     * - 它已经绑定了 application.yaml 中配置的 DeepSeek 模型
     * - 我们调用 .build() 构建出一个最简单的 ChatClient 实例
     * - 如果需要带角色设定、带记忆的 ChatClient，需要在 build 之前配置
     */
    public SimpleChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 【接口1】最简AI对话接口
     * GET /chat?message=你好
     *
     * 调用链解读：
     * chatClient.prompt()  → 创建一个PromptBuilder，开始构建请求
     *   .user(message)     → 设置用户消息内容
     *   .call()            → 同步调用AI模型，等待完整回复
     *   .content()         → 提取回复的文本内容（String类型）
     *
     * 对比：如果用 .stream() 代替 .call()，就是流式调用（逐字输出）
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()   // 开始构建提示词
                .user(message)       // 设置用户消息
                .call()              // 同步调用（阻塞等待AI回复完成）
                .content();          // 提取回复的纯文本内容
    }

    /**
     * 【接口2】带Token统计的AI对话接口
     * GET /chat/info?message=你好
     */
    @GetMapping("/chat/info")
    public Map<String, Object> chatInfo(@RequestParam String message) {
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .call()
                .chatResponse();  // 获取完整响应，包含内容和元数据
        
        String content = Optional.ofNullable(chatResponse.getResult())
                .map(result -> result.getOutput())
                .map(AssistantMessage::getText)
                .orElse("无响应内容");

        //获取Token使用统计
        var usage = chatResponse.getMetadata().getUsage();

        // 用Map组装返回结果（简单场景用Map，复杂场景建议用专用DTO类）
        return Map.of(
                "content", content,                    // AI回复的文本内容
                "promptTokens", usage.getPromptTokens(),       // 输入Token数（你发给AI的）
                "completionTokens", usage.getCompletionTokens(), // 输出Token数（AI回复的）
                "totalTokens", usage.getTotalTokens()          // 总Token数
        );
    }

    /**
     * 【接口3】带错误处理的AI对话接口
     * GET /chat/safe?message=你好
     */
    @GetMapping("/chat/safe")
    public Map<String, Object> chatSafe(@RequestParam String message) {
        try {
            String reply = chatClient.prompt()
                    .user(message)
                    .call()
                    .content();
            // 成功时返回 success=true 和AI回复
            return Map.of("success", true, "reply", reply);
        } catch (Exception e) {
            // 失败时返回 success=false 和友好提示
            // 不把原始异常信息暴露给用户（可能包含敏感信息如API Key）
            return Map.of("success", false, "error", "AI服务暂时不可用：" + e.getMessage());
        }
    }

    /**
     * 【接口4】覆盖模型参数的AI对话接口
     * GET /chat/strict?message=写一首诗
     *
     * 这个接口演示了如何在单次调用中覆盖 application.yaml 中的默认参数
     */
    @GetMapping("/chat/strict")
    public String chatStrict(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .options(ChatOptions.builder()
                        .temperature(0.1)  // 覆盖温度：0.1（非常确定）
                        .maxTokens(500)    // 限制最大输出Token数
                        .build())
                .call()
                .content();
    }
}
```

> 💡 **代码解析**：
> - `ChatClient.Builder` 由 Spring AI 自动配置注入，不需要手动创建 `ChatModel`
> - `.prompt()` 开始构建提示词
> - `.user(message)` 设置用户消息
> - `.call()` 同步调用，`.stream()` 流式调用
> - `.content()` 提取回复文本内容
> - `.chatResponse()` 获取完整响应（含Token统计）
> - `.options(ChatOptions.builder()...)` 可覆盖模型参数

---

### 步骤5：启动项目 + 测试

1. 运行 `SmartApplication.main()`
2. 确认控制台输出：`Started SmartApplication in x.xxx seconds`
3. 浏览器访问：

```
http://localhost:8080/chat?message=你好，请介绍一下你自己
```

4. 应该看到 DeepSeek 返回的文本回复

测试其他接口：
```
http://localhost:8080/chat/info?message=你好
→ 返回JSON：{ "content": "...", "promptTokens": 12, "completionTokens": 45, "totalTokens": 57 }

http://localhost:8080/chat/safe?message=你好
→ 返回JSON：{ "success": true, "reply": "..." }

http://localhost:8080/chat/strict?message=写一首关于春天的诗
→ 多次刷新，输出几乎一致（temperature=0.1的效果）
```

> 🔧 **常见问题**：
> - 如果报 `401 Unauthorized`：API Key 配置错误
> - 如果报 `Connection refused`：检查 base-url 是否正确
> - 如果报 `Model not found`：确认 model 填的是 `deepseek-chat`

---

## Part 2：同步调用五种模式（9:40-10:30）

> **💡 五种模式总览**：从简单到复杂，逐步增强 AI 对话的能力
>
> | 模式 | 核心能力 | 关键方法 | 类比 |
> |------|---------|---------|------|
> | 模式一 | 最简调用 | `.call().content()` | 打电话：说完就挂 |
> | 模式二 | System Prompt | `.system("角色")` | 给对方一个身份 |
> | 模式三 | 多轮对话 | `ChatMemory` + `Advisor` | 记住之前说过的话 |
> | 模式四 | 参数化模板 | `.user(u -> u.text("{key}").param())` | 填表单：模板+参数 |
> | 模式五 | 结构化输出 | `.entity(Xxx.class)` | 对方按格式填表返回 |

### 模式一：最简调用（已完成）

就是上面 Part 1 写的 `.call().content()`，这里不再重复。

---

### 模式二：System Prompt + .system() 方法

> **💡 什么是 System Prompt？**
> System Prompt 是给 AI 设定"角色"和"行为规则"的指令。它告诉 AI："你是谁，你应该怎么回答"。
>
> **类比理解**：就像给客服人员发的工作手册——"你是XX公司的客服，回答要简洁礼貌，不超过50字"。有了这个手册，客服就知道该怎么回答了。
>
> **有无 System Prompt 的区别**：
> - 没有 System Prompt：AI 自由发挥，回答可能很长、偏题、语气不一致
> - 有 System Prompt：AI 按角色设定回答，简洁、准确、语气一致
>
> **`.system()` vs `.defaultSystem()` 的区别**：
> - `.system("角色")`：每次调用时临时指定，适合偶尔切换角色
> - `.defaultSystem("角色")`：在 AiConfig 中预设，适合固定的角色，不需要每次都写
> - 推荐做法：用 `defaultSystem` 预设角色（见 Part 3），用 `.system()` 临时覆盖

创建 `ModeController.java`：

```java
package com.huashan.smart.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 五种同步调用模式演示控制器
 *
 * 本Controller演示模式二（System Prompt）和模式四（参数化模板）
 * 模式一（最简调用）见 SimpleChatController
 * 模式三（ChatMemory）见 MemoryChatController
 * 模式五（结构化输出）见 StructuredController
 */
@RestController
@RequestMapping("/mode")  // 所有接口的URL前缀为 /mode
public class ModeController {

    private final ChatClient chatClient;

    /**
     * 构造器注入 ChatClient.Builder
     * 这里和 SimpleChatController 一样，构建一个最基本的 ChatClient
     * （不带预设角色，不带记忆——这些由每次调用时按需指定）
     */
    public ModeController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    // ==================== 模式二：System Prompt ====================

    /**
     * 【模式二-A】每次调用时临时指定 System Prompt
     * GET /mode/system?question=宿舍几点熄灯
     *
     * 调用链解读：
     * .system("你是高校宿舍管理助手...")  → 设置系统角色（告诉AI"你是谁"）
     * .user(question)                     → 设置用户问题
     * .call().content()                    → 同步调用并提取文本
     *
     * 效果：AI会按照"宿舍管理助手"的角色来回答，回答简洁不超过50字
     */
    @GetMapping("/system")
    public String withSystemPrompt(@RequestParam String question) {
        return chatClient.prompt()
                .system("你是高校宿舍管理助手，回答简洁，不超过50字。")  // 设置AI角色
                .user(question)      // 用户的问题
                .call()
                .content();
    }


    // ==================== 模式四：参数化Prompt模板 ====================

    /**
     * 【模式四】参数化 Prompt 模板
     * GET /mode/template?building=3&student=张三
     *
     * 什么是参数化模板？
     * - 把 Prompt 中变化的部分用 {占位符} 表示
     * - 通过 .param("占位符名", 实际值) 传入具体值
     * - 类似 SQL 的预编译：SELECT * FROM student WHERE building = ?
     *
     * 为什么需要参数化模板？
     * 1. 安全：防止用户输入干扰 Prompt 结构（类似防SQL注入）
     * 2. 复用：同一个模板可以传入不同参数
     * 3. 维护：Prompt 文本和业务数据分离，修改方便
     *
     * 代码解读：
     * .user(u -> u.text("...{building}...{student}...")
     *         .param("building", building)   → 将 {building} 替换为实际值
     *         .param("student", student))     → 将 {student} 替换为实际值
     */
    @GetMapping("/template")
    public String withTemplate(@RequestParam String building,
                               @RequestParam String student) {
        return chatClient.prompt()
                .system("你是宿舍管理助手，回答简洁。")
                .user(u -> u.text("帮我查{building}楼的{student}的考勤记录，并给出分析")
                        .param("building", building)    // {building} → 实际的楼号
                        .param("student", student))     // {student}  → 实际的学生名
                .call()
                .content();
    }
}
```

测试对比：
```
http://localhost:8080/mode/system?question=宿舍几点熄灯
→ "宿舍熄灯时间为晚上11:00。"

http://localhost:8080/mode/default?question=宿舍几点熄灯
→ "关于宿舍熄灯时间，不同学校、不同宿舍楼的规定可能有所不同..."（长篇大论）

http://localhost:8080/mode/template?building=3&student=张三
http://localhost:8080/mode/template2?building=3&room=301&month=6
```

---

### 模式三：ChatMemory 多轮对话

> **💡 为什么需要 ChatMemory？**
>
> 没有 ChatMemory 时，每次调用 AI 都是"失忆"的——它不记得你之前说过什么。比如：
> - 你说"我叫张三住301" → AI回复"你好张三"
> - 你接着问"我住哪" → AI回答"抱歉，我不知道你的住宿信息"
>
> 有了 ChatMemory，AI 能"记住"之前的对话：
> - 你说"我叫张三住301" → AI回复"你好张三"（这条对话被存入Memory）
> - 你问"我住哪" → Advisor自动从Memory中取出历史消息，AI知道你之前说过住301
>
> **ChatMemory 的工作原理**：
> ```
> 请求流程：
> 1. 用户发送消息 → 2. Advisor前置处理（从Memory取历史消息注入Prompt）
>                     → 3. 发送给AI模型 → 4. AI回复
>                     → 5. Advisor后置处理（将本次对话存入Memory）
> ```
>
> **conversationId 的作用**：不同 conversationId 对应不同的对话空间。就像不同的微信群——你在A群说的话，B群的人看不到。这样不同用户的对话互不干扰。

这是最核心的模式。先创建配置类，再创建 Controller。

**1. 创建 AiConfig 配置类**

创建包 `com.huashan.smart.config`，新建 `AiConfig.java`：

```java
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
 * Spring AI 核心配置类
 *
 * 这个类负责配置 Spring AI 的核心组件：
 * 1. ChatMemory     - 对话记忆（让AI记住之前的对话）
 * 2. ChatClient     - AI对话客户端（带角色+记忆）
 * 3. Prompt Bean    - 集中管理的Prompt文本
 *
 * @Configuration 告诉Spring这是一个配置类，里面的 @Bean 方法返回的对象
 * 会被Spring容器管理，其他类可以通过 @Autowired 或 @Qualifier 注入使用
 */
@Configuration
public class AiConfig {

    /**
     * 创建 ChatMemory Bean（对话记忆）
     *
     * ChatMemory 的三个核心概念：
     * 1. ChatMemory 接口        - 定义了"添加消息/获取消息/清除消息"的能力
     * 2. MessageWindowChatMemory - 基于窗口的实现，只保留最近N条消息
     * 3. InMemoryChatMemoryRepository - 存储方式（内存），重启后数据丢失
     *
     * 构建方式解读：
     * MessageWindowChatMemory.builder()
     *     .chatMemoryRepository(new InMemoryChatMemoryRepository())
     *       → 使用内存存储（开发/测试用，生产环境应换成Redis等持久化存储）
     *     .maxMessages(20)
     *       → 最多保留20条消息（包括用户消息和AI回复）
     *       → 超过20条时，最旧的消息会被丢弃
     *       → 为什么要限制？因为每条消息都会消耗Token，消息越多成本越高
     *     .build();
     *
     * ⚠️ InMemoryChatMemoryRepository 的局限性：
     * - 数据存储在JVM内存中，应用重启后所有对话历史丢失
     * - 适合开发和测试，生产环境需要用持久化存储（如Redis）
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())  // 内存存储
                .maxMessages(20)  // 窗口大小：只保留最近20条消息
                .build();
    }

    /**
     * 创建宿舍助手 ChatClient Bean
     *
     * 这个Bean做了两件事：
     * 1. defaultSystem() - 预设角色：宿舍管理助手"小宿"
     * 2. defaultAdvisors() - 预设Advisor：自动维护对话上下文
     *
     * 参数解读：
     * @param chatClientBuilder Spring AI自动注入的Builder（已绑定DeepSeek模型）
     * @param chatMemory        上面定义的ChatMemory Bean
     *
     * 构建方式解读：
     * chatClientBuilder
     *     .defaultSystem("...")  → 预设System Prompt（AI的角色和行为规则）
     *       每次调用这个ChatClient时，都会自动带上这个System Prompt
     *       不需要每次 .system()，代码更简洁
     *
     *     .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
     *       → 注册ChatMemory的Advisor
     *       每次调用时，Advisor会：
     *         前置处理：从ChatMemory中取出历史消息，注入到Prompt中
     *         后置处理：将本次的用户消息和AI回复保存到ChatMemory
     *       你不需要手动管理消息列表，Advisor全自动处理！
     *
     *     .build();  → 构建出最终的ChatClient实例
     *
     * ⚠️ Bean名称"dormitoryChatClient"很重要：
     * - 其他类通过 @Qualifier("dormitoryChatClient") 注入这个特定的ChatClient
     * - 区别于"tutorChatClient"（编程老师），两个ChatClient有不同角色
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
}
```

> ⚠️ **关键点**：
> - `MessageWindowChatMemory`：基于窗口的对话记忆，保留最近20条消息，重启后丢失。生产环境可用 Redis 等持久化
> - `InMemoryChatMemoryRepository`：内存存储实现
> - `MessageChatMemoryAdvisor.builder(chatMemory).build()`：Advisor 模式自动维护上下文
> - `.defaultAdvisors()` 注册 Advisor，每次调用自动维护上下文

**2. 创建 MemoryChatController**

```java
package com.huashan.smart.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 多轮对话演示控制器
 * 演示ChatMemory自动维护上下文
 *
 * 和 SimpleChatController 的区别：
 * - SimpleChatController 注入的是 ChatClient.Builder（最基础的，无记忆）
 * - 本Controller 注入的是 @Qualifier("dormitoryChatClient")（带角色+记忆）
 */
@RestController
@RequestMapping("/memory")
public class MemoryChatController {

    private final ChatClient chatClient;

    public MemoryChatController(@Qualifier("dormitoryChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 多轮对话接口
     * GET /memory/chat?conversationId=user001&message=我叫张三住301
     * GET /memory/chat?conversationId=user001&message=我住哪
     *
     * 参数解读：
     * - conversationId：会话ID，用于区分不同用户/不同对话
     *   就像"聊天窗口"——不同的 conversationId 是不同的聊天窗口
     *   同一个 conversationId 下的对话，AI都能记住
     * - message：用户消息
     */
    @GetMapping("/chat")
    public String chat(@RequestParam String conversationId,
                       @RequestParam String message) {
        return chatClient.prompt()
                .user(message)  // 设置用户消息
                // 向ChatMemory Advisor传递conversationId参数
                // 这样Advisor就知道该从哪个"聊天窗口"存取历史消息
                .advisors(advisor -> advisor.param("chat_memory_conversation_id", conversationId))
                .call()
                .content();
    }
}
```

**3. 测试多轮对话**

第一次请求：
```
http://localhost:8080/memory/chat?conversationId=user001&message=我叫张三住301
→ "你好张三同学！已记录你住在301宿舍，有什么可以帮你的吗？"
```

第二次请求（同一 conversationId）：
```
http://localhost:8080/memory/chat?conversationId=user001&message=我住哪
→ "你住在301宿舍，张三同学。"
```

换一个 conversationId（不同对话）：
```
http://localhost:8080/memory/chat?conversationId=user002&message=我住哪
→ "抱歉，我还不了解你的住宿信息，请问你叫什么名字？住哪个宿舍？"
```

> 💡 **对话隔离原理**：ChatMemory 内部使用类似 Map 的结构，`conversationId` 是 Key，对话历史是 Value。不同的 `conversationId` 对应不同的对话空间，互不干扰。就像你微信和不同人的聊天记录是分开的一样。

---

### 模式五：结构化输出 Entity

> **💡 什么是结构化输出？**
>
> 普通 AI 回复是自由格式的文本，你可能收到"张三住在3号楼301"，也可能收到"3号楼301住着张三"。格式不固定，程序很难自动解析。
>
> 结构化输出让 AI 按照预定义的 Java 类格式返回数据，自动转为 Java 对象。就像让填表——你设计好表格（Entity类），AI负责填内容。
>
> **`.entity()` 的工作原理**：
> ```
> 1. 你定义 StudentInfo 类（name, building, room, phone, attendanceStatus）
> 2. 调用 .entity(StudentInfo.class)
> 3. Spring AI 内部自动：
>    a. 根据类的字段生成 JSON Schema（告诉AI"你必须返回这种格式的JSON"）
>    b. 把 Schema 要求追加到 System Prompt 中
>    c. AI 按要求返回 JSON 字符串
>    d. Jackson 自动将 JSON 反序列化为 StudentInfo 对象
> 4. 你拿到的直接就是 StudentInfo 对象，不需要手动解析JSON！
> ```

**1. 创建 Entity 类**

创建包 `com.huashan.smart.model`，新建 `StudentInfo.java`：

```java
package com.huashan.smart.model;

import lombok.Data;

/**
 * 学生信息实体（用于AI结构化输出）
 *
 * 这个类的字段名就是AI返回JSON的Key名：
 * - AI会返回 {"name":"李四", "building":"5", "room":"203", ...}
 * - Spring AI 自动将这个JSON映射为StudentInfo对象
 *
 * ⚠️ 字段命名规范：
 * - 字段名必须和System Prompt中要求的Key一致
 * - 使用驼峰命名法，Jackson会自动映射
 * - 类型都是String，因为AI返回的都是文本，需要数字的话可以自行转换
 *
 * @Data 注解（Lombok）自动生成：
 * - 所有字段的 getter/setter
 * - toString() 方法
 * - equals() / hashCode() 方法
 * - 全参构造器（通过@AllArgsConstructor可以额外添加）
 */
@Data
public class StudentInfo {
    /** 姓名 - AI会从用户描述中提取人名填入 */
    private String name;
    /** 宿舍楼号 - AI会从"3号楼"中提取出"3" */
    private String building;
    /** 房间号 - AI会从"301室"中提取出"301" */
    private String room;
    /** 手机号 - AI会提取出11位手机号 */
    private String phone;
    /** 考勤状态 - AI会从"本月缺勤2次"中提取出"缺勤2次" */
    private String attendanceStatus;
}
```

**2. 创建 StructuredController**

```java
package com.huashan.smart.controller;

import com.huashan.smart.model.StudentInfo;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 结构化输出演示控制器
 * AI回复自动映射为Java对象
 *
 * 核心方法：.entity(StudentInfo.class)
 * 代替了 .content()（返回String），直接返回Java对象
 */
@RestController
@RequestMapping("/struct")
public class StructuredController {

    private final ChatClient chatClient;

    public StructuredController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 结构化输出：AI回复自动转为StudentInfo对象
     * GET /struct/student?desc=张三住3号楼301，手机13800138000，本月全勤
     */
    @GetMapping("/student")
    public StudentInfo extractStudent(@RequestParam String desc) {
        return chatClient.prompt()
                .system("从用户描述中提取学生信息，按JSON格式返回，字段：name/building/room/phone/attendanceStatus")
                .user(desc)
                .call()
                .entity(StudentInfo.class);  // ← 关键：用 .entity() 代替 .content()
    }
}
```

测试：
```
http://localhost:8080/struct/student?desc=李四住5号楼203，手机13912345678，本月缺勤2次
```

返回：
```json
{
  "name": "李四",
  "building": "5",
  "room": "203",
  "phone": "13912345678",
  "attendanceStatus": "缺勤2次"
}
```

> 💡 **`.entity()` 的魔法**：Spring AI 内部使用 `BeanOutputConverter`，自动生成 JSON Schema 约束 AI 输出格式，然后将 JSON 反序列化为 Java 对象。整个过程你只需要定义一个 Entity 类，调用 `.entity()` 一行代码即可。

---

## 课间休息（10:30-10:40）

---

## Part 3：角色管理 + Prompt 模板（10:40-11:25）

### 步骤8：完善 AiConfig 配置类——多角色 ChatClient + RTF Prompt

> **💡 为什么需要多个 ChatClient？**
>
> 在实际项目中，你可能需要不同的AI角色：
> - 宿舍助手"小宿"：回答宿舍管理问题
> - 编程老师"王老师"：解答Java编程问题
> - 报修助手"小修"：引导填写报修单
>
> 每个角色有不同的 System Prompt、不同的行为规则。如果只用一个 ChatClient，
> 每次调用都要 `.system("...")` 指定角色，代码又长又容易出错。
>
> **解决方案**：在 AiConfig 中为每个角色创建一个独立的 ChatClient Bean，
> 各自预设好 defaultSystem 和 defaultAdvisors。使用时通过 @Qualifier 注入。
>
> **类比理解**：就像公司里有不同的客服岗位——A同事负责售前，B同事负责售后。
> 你不需要每次都告诉A同事"你是售前客服"，因为A同事入职时就已经培训好了。

修改 `AiConfig.java`，添加更多角色的 ChatClient Bean 和 RTF Prompt：

```java
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
 * 配置ChatClient、ChatMemory等核心组件
 *
 * 这个配置类管理三类Bean：
 * 1. ChatMemory          - 对话记忆（共享给所有ChatClient）
 * 2. ChatClient（多个）  - 不同的AI角色（宿舍助手、编程老师）
 * 3. String（Prompt）    - RTF框架的Prompt文本（集中管理）
 */
@Configuration
public class AiConfig {

    /**
     * 内存版ChatMemory
     * （解释见 Part 2 模式三，此处不再重复）
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(20)
                .build();
    }

    /**
     * 宿舍助手ChatClient
     * 角色定位：宿舍管理相关问题的AI助手
     *
     * Bean名称："dormitoryChatClient"
     * 使用方式：@Qualifier("dormitoryChatClient") ChatClient chatClient
     * 默认角色：高校智能宿舍管理系统的AI助手"小宿"
     * 默认能力：ChatMemory（自动维护多轮对话上下文）
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
     * 编程老师ChatClient
     * 角色定位：Java编程教学助手
     *
     * Bean名称："tutorChatClient"
     * 使用方式：@Qualifier("tutorChatClient") ChatClient chatClient
     * 默认角色：经验丰富的Java编程老师"王老师"
     * 默认能力：ChatMemory（自动维护多轮对话上下文）
     *
     * ⚠️ 注意：dormitoryChatClient 和 tutorChatClient 共享同一个 ChatMemory Bean
     * 但它们的对话是隔离的——因为 conversationId 不同，各自有各自的对话空间
     * 就像两个人用同一个微信App，但各自的聊天记录是分开的
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
     * 宿舍助手完整Prompt（RTF框架：Role-Task-Format）
     *
     * 什么是RTF框架？
     * - R = Role（角色）：定义AI的身份——"你是谁"
     * - T = Task（任务）：定义AI要做什么——"你要处理什么类型的请求"
     * - F = Format（格式）：定义输出格式——"你应该怎么回答"
     *
     * 为什么要用RTF框架？
     * - 结构化Prompt，让每个部分职责清晰
     * - 方便团队协作和维护（改角色改任务改格式，互不影响）
     * - 比纯文本Prompt更容易理解和修改
     *
     * 为什么要把Prompt定义为Bean？
     * - 集中管理：所有Prompt在一个配置类中，修改方便
     * - 避免硬编码：不在Controller中直接写长文本
     * - 方便复用：多个Controller可以注入同一个Prompt
     *
     * 使用方式：@Qualifier("dormitoryPrompt") String dormitoryPrompt
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
```

---

### 步骤9：多角色对话测试

创建 `RoleChatController.java`：

```java
package com.huashan.smart.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 多角色对话控制器
 * 演示不同ChatClient Bean的不同角色设定
 *
 * 核心知识点：
 * 1. @Qualifier 注入不同角色的 ChatClient
 * 2. .system() 可以临时覆盖 defaultSystem 设定的角色
 * 3. 同一个问题，不同角色会有不同的回答风格和内容
 */
@RestController
@RequestMapping("/role")
public class RoleChatController {

    // 两个不同角色的ChatClient
    private final ChatClient dormitoryClient;  // 宿舍助手"小宿"
    private final ChatClient tutorClient;      // 编程老师"王老师"

    /**
     * 构造器注入两个ChatClient
     *
     * @Qualifier 的作用：
     * - Spring容器中有多个 ChatClient 类型的 Bean
     * - 不加 @Qualifier 的话，Spring不知道该注入哪一个，会报错
     * - "dormitoryChatClient" → 注入宿舍助手
     * - "tutorChatClient" → 注入编程老师
     *
     * 类比理解：就像员工分配——公司里有很多员工（Bean），
     * 你需要指定姓名（@Qualifier）才能找到正确的人
     */
    public RoleChatController(
            @Qualifier("dormitoryChatClient") ChatClient dormitoryClient,
            @Qualifier("tutorChatClient") ChatClient tutorClient) {
        this.dormitoryClient = dormitoryClient;
        this.tutorClient = tutorClient;
    }

    /**
     * 宿舍助手对话
     * GET /role/dormitory?message=301室有多少人
     *
     * 调用方式：直接用 dormitoryClient.prompt().user(message).call().content()
     * 不需要 .system()，因为 defaultSystem 已经预设了宿舍助手角色
     */
    @GetMapping("/dormitory")
    public String dormitoryChat(@RequestParam String message) {
        return dormitoryClient.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * 编程老师对话
     * GET /role/tutor?message=什么是Spring Boot
     *
     * 调用方式：和宿舍助手完全一样，只是用的不同角色的ChatClient
     * 同样不需要 .system()，因为 tutorChatClient 自带了编程老师角色
     */
    @GetMapping("/tutor")
    public String tutorChat(@RequestParam String message) {
        return tutorClient.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     * 同一问题，不同角色对比
     * GET /role/compare?message=帮我查成绩
     *
     * 这是一个有趣的对比实验：
     * - 同一个问题"帮我查成绩"，两个角色的回答完全不同
     * - 宿舍助手：关心你的考勤成绩（宿舍管理相关）
     * - 编程老师：关心你的编程课程成绩（教学相关）
     *
     * 这体现了 defaultSystem 的价值——同一个模型，不同的角色设定产生不同的回答
     */
    @GetMapping("/compare")
    public Map<String, String> compare(@RequestParam String message) {
        // 宿舍助手的回答
        String dormReply = dormitoryClient.prompt()
                .user(message)
                .call()
                .content();

        // 编程老师的回答
        String tutorReply = tutorClient.prompt()
                .user(message)
                .call()
                .content();

        // 用Map返回两者的对比结果
        return Map.of(
                "宿舍助手小宿", dormReply,
                "编程老师王老师", tutorReply
        );
    }

   
}
```

测试对比：
```
http://localhost:8080/role/compare?message=帮我查成绩
→ {
    "宿舍助手小宿": "请告诉我你的姓名或学号，我帮你查询本学期的考勤成绩。",
    "编程老师王老师": "你想查什么编程课程的成绩？可以告诉我具体的课程名..."
  }

http://localhost:8080/role/temp?message=写一首诗
→ （虽然是宿舍助手，但这次回答像诗人）
```

---

### 步骤10：RTF 框架 Prompt 代码化

> **💡 RTF框架和直接写 System Prompt 的区别**：
>
> 直接写 System Prompt：一大段文字，角色、任务、格式混在一起，难以维护
> RTF框架：用 `# Role`、`# Task`、`# Format` 三个标题分段，职责清晰
>
> 就像写代码——没有分层的代码全挤在一个方法里，有分层的代码按职责分开。
> RTF就是Prompt的"分层架构"。
>
> **为什么把Prompt定义为Bean？**
> - 直接在Controller中写长文本 → 代码臃肿，难以维护
> - 定义为Bean → 集中管理，修改方便，可复用
> - 类比：把硬编码的SQL提取到Mapper XML文件中

创建使用 RTF Prompt Bean 的 Controller：

```java
package com.huashan.smart.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/rtf")
public class RtfPromptController {

    private final ChatClient chatClient;
    // RTF框架的Prompt文本（从AiConfig注入）
    private final String dormitoryPrompt;

    /**
     * 构造器注入
     *
     * @param chatClientBuilder  Spring AI 自动配置的Builder
     * @param dormitoryPrompt     AiConfig中定义的RTF Prompt Bean
     *   通过 @Qualifier("dormitoryPrompt") 指定注入哪个String类型的Bean
     *
     */
    public RtfPromptController(ChatClient.Builder chatClientBuilder,
                               @Qualifier("dormitoryPrompt") String dormitoryPrompt) {
        this.chatClient = chatClientBuilder.build();
        this.dormitoryPrompt = dormitoryPrompt;
    }

    /**
     * 使用RTF框架Prompt的AI对话
     * GET /rtf/chat?message=我的宿舍水龙头坏了
    */
    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
                .system(dormitoryPrompt)  // 使用RTF框架的Prompt（从Bean注入）
                .user(message)
                .call()
                .content();
    }
}
```

测试三种场景：
```
http://localhost:8080/rtf/chat?message=我的宿舍水龙头坏了
→ 报修引导

http://localhost:8080/rtf/chat?message=新生入住需要什么手续
→ 入住咨询

http://localhost:8080/rtf/chat?message=宿舍几点熄灯
→ 纪律查询
```

---

## Part 4：完整 AI 服务后端实现（11:25-12:00）

> **💡 为什么要封装 Service 层？**
>
> 之前的 Controller 直接调用 ChatClient，简单但有以下问题：
> - 业务逻辑和HTTP接口混在一起
> - 无法复用（其他地方也需要AI对话怎么办？）
> - 没有统一的异常处理
> - 没有统一的响应格式
>
> **三层架构**：
> ```
> Controller层  →  接收请求、封装响应（只管HTTP协议相关的事）
> Service层    →  业务逻辑、异常处理（管AI调用和错误处理）
> ChatClient层 →  AI对话（Spring AI提供，不需要我们写）
> ```
>
> 类比理解：
> - Controller = 前台接待（接单、交货）
> - Service = 后厨（做菜、处理食材问题）
> - ChatClient = 食材供应商（提供原材料）

### 步骤11：创建 AiChatService 接口

创建包 `com.huashan.smart.service`，新建 `AiChatService.java`：

```java
package com.huashan.smart.service;

import reactor.core.publisher.Flux;

/**
 * AI聊天Service接口
 * 提供同步对话和流式对话能力
 *
 * 为什么用接口+实现类，不直接写实现类？
 * 1. 面向接口编程：Controller依赖接口，不依赖具体实现
 * 2. 方便替换：未来换实现（比如加缓存、换模型）不需要改Controller
 * 3. 方便测试：可以Mock接口做单元测试
 *
 * 两个方法对比：
 * - chat()      → 同步，等AI回复完再返回（适合简短问答）
 * - chatStream() → 流式，边生成边返回（适合长文回复，体验更好）
 */
public interface AiChatService {

    /**
     * 同步对话
     * @param conversationId 会话ID（用于多轮对话隔离，不同用户用不同的ID）
     * @param message 用户消息
     * @return AI回复内容
     */
    String chat(String conversationId, String message);

    /**
     * 流式对话（Flux）
     * @param conversationId 会话ID
     * @param message 用户消息
     * @return 流式响应（Flux<String> = 多个String元素的异步流）
     *
     * 什么是Flux<String>？
     * - Flux 是 Reactor 框架的响应式类型，表示"0个或多个元素的异步序列"
     * - Flux<String> 在这里表示"AI的回复被拆成多个String片段，逐个推送"
     * - 每个String片段就是AI生成的一个词或一句话
     * - Spring 自动将 Flux<String> 转为 SSE（Server-Sent Events）格式推送
     * - 前端用 EventSource 接收，实现打字机效果
     */
    Flux<String> chatStream(String conversationId, String message);
}
```

---

### 步骤12：实现 AiChatServiceImpl

创建 `com.huashan.smart.service.impl` 包，新建 `AiChatServiceImpl.java`：

```java
package com.huashan.smart.service.impl;

import com.huashan.smart.service.AiChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * AI聊天Service实现类
 * 基于Spring AI 1.0.0 GA + ChatClient + ChatMemory实现
 *
 * 核心设计：
 * 1. 注入 dormitoryChatClient（自带角色+记忆，不需要额外配置）
 * 2. chat() 方法：同步调用 + 异常处理
 * 3. chatStream() 方法：流式调用 + 异常处理
 *
 * @Slf4j  Lombok注解，自动生成 log 对象（等价于 private static final Logger log = ...）
 * @Service Spring注解，标记为业务层组件，自动注册为Bean
 */
@Slf4j
@Service
public class AiChatServiceImpl implements AiChatService {

    private final ChatClient chatClient;

    /**
     * 构造器注入宿舍助手ChatClient
     *
     * @Qualifier("dormitoryChatClient") 指定注入宿舍助手的ChatClient
     * 这个ChatClient自带：
     * - defaultSystem：宿舍助手"小宿"的角色设定
     * - defaultAdvisors：ChatMemory Advisor（自动维护多轮对话上下文）
     *
     * 所以Service层不需要再写 .system() 或手动管理消息列表
     * 只需要传 conversationId（告诉Advisor用哪个对话空间）和message（用户消息）
     */
    public AiChatServiceImpl(@Qualifier("dormitoryChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 同步对话实现
     *
     * 执行流程：
     * 1. chatClient.prompt()  → 开始构建请求
     * 2. .user(message)       → 设置用户消息
     * 3. .advisors(...)       → 传递conversationId给ChatMemory Advisor
     *    → Advisor前置处理：从Memory取历史消息注入Prompt
     * 4. .call()              → 同步调用AI模型，等待完整回复
     *    → Advisor后置处理：将本次对话存入Memory
     * 5. .content()           → 提取回复文本
     *
     * 为什么用try-catch？
     * - AI API可能超时、密钥过期、服务不可用
     * - 不捕获异常会导致Controller层收到500错误
     * - 捕获后返回友好提示，用户体验更好
     */
    @Override
    public String chat(String conversationId, String message) {
        try {
            return chatClient.prompt()
                    .user(message)
                    // 向ChatMemory Advisor传递conversationId
                    // 这样Advisor就知道该从哪个"对话空间"存取历史消息
                    .advisors(advisor -> advisor
                            .param("chat_memory_conversation_id", conversationId))
                    .call()       // 同步调用
                    .content();   // 提取文本
        } catch (Exception e) {
            // 记录错误日志（方便排查问题）
            log.error("AI同步对话异常：", e);
            // 返回友好提示（不暴露技术细节给用户）
            return "AI服务暂时不可用，请稍后再试。";
        }
    }

    /**
     * 流式对话实现
     *
     * 和 chat() 的区别：
     * - .call() → .stream()    同步变流式
     * - 返回 Flux<String>       代替 String
     *
     * .stream().content() 的含义：
     * - .stream() 返回一个流式响应对象
     * - .content() 将流式响应转为 Flux<String>（每个元素是一个文本片段）
     * - Spring 自动将 Flux<String> 转为 SSE 格式推送给前端
     *
     * SSE格式示例：
     * data:你
     * data:好
     * data:！
     * data:我
     * data:是
     * （每行一个文本片段，前端逐行接收实现打字机效果）
     */
    @Override
    public Flux<String> chatStream(String conversationId, String message) {
        try {
            return chatClient.prompt()
                    .user(message)
                    .advisors(advisor -> advisor
                            .param("chat_memory_conversation_id", conversationId))
                    .stream()    // ← 关键区别：.stream() 代替 .call()
                    .content();  // 返回 Flux<String>
        } catch (Exception e) {
            log.error("AI流式对话异常：", e);
            // Flux.just() 创建一个只包含一个元素的流
            // 相当于同步返回一个错误消息，但包装成了Flux格式
            return Flux.just("AI服务暂时不可用，请稍后再试。");
        }
    }
}
```

> 💡 **核心设计**：
> - 注入 `dormitoryChatClient`（自带 defaultSystem + ChatMemory）
> - 通过 `chat_memory_conversation_id` 参数实现会话隔离
> - 同步用 `.call().content()`，流式用 `.stream().content()`
> - 流式返回 `Flux<String>`，Spring 自动转为 SSE 推送

---

### 步骤13：创建统一响应类 Result

创建包 `com.huashan.smart.common`，新建 `Result.java`：

```java
package com.huashan.smart.common;

import lombok.Data;

/**
 * 统一响应结果类
 *
 * 为什么需要统一响应格式？
 * - 没有统一格式：有的接口返回String，有的返回Map，有的返回对象
 *   前端不知道怎么解析，每种接口都要单独处理
 * - 有了统一格式：所有接口都返回 { code, message, data }
 *   前端统一判断 code==200 表示成功，取 data 即可
 *
 * 泛型 <T> 的含义：
 * - T 是数据类型参数，使用时指定具体类型
 * - Result<String> → data是String类型
 * - Result<StudentInfo> → data是StudentInfo类型
 * - 好处：编译时就能检查类型，避免运行时类型错误
 *
 * @Data 注解自动生成 getter/setter/toString 等
 */
@Data
public class Result<T> {

    /** 状态码：200=成功，500=失败 */
    private int code;
    /** 提示信息 */
    private String message;
    /** 数据：成功时为具体数据，失败时为null */
    private T data;

    /**
     * 成功响应的静态工厂方法
     *
     * 用法：Result.success("AI回复内容")
     * 返回：{ "code": 200, "message": "操作成功", "data": "AI回复内容" }
     *
     * 为什么用静态工厂方法而不是构造器？
     * - 方法名有意义：success() 比 new Result() 更清晰
     * - 可以省略 code 和 message 的设置
     * - 调用更简洁：Result.success(data) 一行搞定
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);           // 200 = HTTP成功状态码
        result.setMessage("操作成功");
        result.setData(data);          // 实际数据
        return result;
    }

    /**
     * 失败响应的静态工厂方法
     *
     * 用法：Result.error("AI服务不可用")
     * 返回：{ "code": 500, "message": "AI服务不可用", "data": null }
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);           // 500 = 服务器错误状态码
        result.setMessage(message);    // 错误提示信息
        // data 不设置，默认为null
        return result;
    }
}
```

---

### 步骤14：创建 AiChatController

创建 `com.huashan.smart.controller.AiChatController`：

```java
package com.huashan.smart.controller;

import com.huashan.smart.common.Result;
import com.huashan.smart.service.AiChatService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * AI聊天控制器
 * 提供同步对话和流式SSE对话接口
 *
 * 这是项目的"正式"AI对话接口（区别于之前的教学演示接口）
 * 采用了完整的分层架构：Controller → Service → ChatClient
 * 使用统一的Result响应格式
 *
 * 两个接口对比：
 * - POST /api/aichat          → 同步对话（等AI回复完再返回）
 * - GET  /api/aichat/stream   → 流式对话（边生成边推送，打字机效果）
 */
@RestController
@RequestMapping("/api/aichat")
public class AiChatController {

    @Autowired  // 注入AiChatService（Spring自动找到AiChatServiceImpl实现类）
    private AiChatService aiChatService;

    /**
     * 【接口1】同步对话
     * POST /api/aichat
     * 请求体：{ "conversationId": "user001", "message": "今天有多少人缺勤？" }
     *
     * 为什么用POST而不是GET？
     * - REST规范：GET用于查询（幂等），POST用于创建/提交
     * - 发送消息属于"创建对话"，用POST更规范
     * - POST支持请求体（RequestBody），可以传更复杂的参数
     * - GET参数在URL中，长文本消息可能导致URL过长
     *
     * @RequestBody 注解：将请求体的JSON自动映射为ChatRequest对象
     * Jackson自动完成JSON→Java对象的转换
     */
    @PostMapping
    public Result<String> chat(@RequestBody ChatRequest chatRequest) {
        // 调用Service层：传入会话ID和用户消息
        String reply = aiChatService.chat(
                chatRequest.getConversationId(),  // 会话ID（用于多轮对话隔离）
                chatRequest.getMessage()           // 用户消息
        );
        // 用统一的Result格式包装返回
        return Result.success(reply);
    }

    /**
     * 【接口2】流式对话（SSE）
     * GET /api/aichat/stream?conversationId=user001&message=你好
     * 返回text/event-stream流，前端使用EventSource接收
     *
     * 关键注解解读：
     * @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
     *   → produces = TEXT_EVENT_STREAM_VALUE 声明返回类型是 SSE
     *   → 告诉浏览器"我的响应是事件流，不要等完整响应，边接收边处理"
     *
     * 为什么用GET而不是POST？
     * - SSE（Server-Sent Events）协议只支持GET请求
     * - 浏览器的 EventSource API 只能发起GET请求
     * - 如果需要POST方式的流式，需要用 fetch + ReadableStream（更复杂）
     *
     * 返回类型 Flux<String> 的含义：
     * - 不是一次返回完整字符串，而是返回一个"字符串流"
     * - 每个元素是AI生成的一个文本片段
     * - Spring 自动将每个元素包装为 SSE 格式：data:文本片段
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestParam String conversationId,
                                   @RequestParam String message) {
        // 直接返回Flux，Spring自动处理SSE转换
        return aiChatService.chatStream(conversationId, message);
    }

    /**
     * 聊天请求体（内部类）
     *
     * 为什么定义成Controller的内部类？
     * - 这个类只在这个Controller中使用，不需要独立存在
     * - 减少类的数量，代码更紧凑
     * - 如果其他Controller也需要，再提取为独立类
     *
     * @Data 自动生成 getter/setter
     * @RequestBody 会将JSON的key和字段名一一对应映射
     */
    @Data
    public static class ChatRequest {
        /** 会话ID（不传则默认为"default"）
         *  不同用户/不同对话用不同的conversationId
         *  同一个conversationId下的对话AI都能记住 */
        private String conversationId = "default";
        /** 用户消息（必填） */
        private String message;
    }
}
```

> ⚠️ **关键点**：
> - `produces = MediaType.TEXT_EVENT_STREAM_VALUE` 声明返回 SSE 流
> - 返回 `Flux<String>` 后，Spring 自动将每个元素作为 SSE `data:` 行推送
> - 同步接口用 `POST`（符合 REST 规范），流式接口用 `GET`（SSE 只支持 GET）

---

### 步骤15：完整测试

**1. 测试同步接口**

使用 Postman 或 curl：

```bash
curl -X POST http://localhost:8080/api/aichat \
  -H "Content-Type: application/json" \
  -d '{"conversationId":"user001","message":"我叫张三住301"}'
```

返回：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "你好张三同学！已记录你住在301宿舍，有什么可以帮你的吗？"
}
```

继续多轮：
```bash
curl -X POST http://localhost:8080/api/aichat \
  -H "Content-Type: application/json" \
  -d '{"conversationId":"user001","message":"我住哪"}'
```

返回：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "你住在301宿舍，张三同学。"
}
```

**2. 测试流式接口**

浏览器访问：
```
http://localhost:8080/api/aichat/stream?conversationId=user001&message=讲个笑话
```

可以看到文字逐字出现（打字机效果）。

> **💡 流式接口在浏览器中看到的效果**：
> 浏览器会持续显示AI生成的文本，每收到一个片段就追加显示，
> 而不是等AI完整回复后再一次性显示。这就是ChatGPT的打字机效果。

---

## Day1 完整项目文件清单

```
src/
├── main/
│   ├── java/com/huashan/smart/
│   │   ├── SmartApplication.java
│   │   ├── common/
│   │   │   └── Result.java
│   │   ├── config/
│   │   │   └── AiConfig.java
│   │   ├── controller/
│   │   │   ├── SimpleChatController.java    ← 最简+Token统计+错误处理+参数覆盖
│   │   │   ├── ModeController.java          ← System Prompt+参数化模板
│   │   │   ├── MemoryChatController.java    ← 多轮对话
│   │   │   ├── StructuredController.java    ← 结构化输出
│   │   │   ├── RoleChatController.java      ← 多角色对话
│   │   │   ├── RtfPromptController.java     ← RTF框架Prompt
│   │   │   └── AiChatController.java        ← 完整AI服务接口
│   │   ├── model/
│   │   │   └── StudentInfo.java             ← 结构化输出Entity
│   │   └── service/
│   │       ├── AiChatService.java           ← 服务接口
│   │       └── impl/
│   │           └── AiChatServiceImpl.java   ← 服务实现
│   └── resources/
│       └── application.yaml
└── pom.xml
```

---

## Day1 知识点总结

| 序号 | 知识点 | 核心代码 | 关键要点 |
|------|--------|---------|---------|
| 1 | 项目搭建 | `spring-ai-starter-model-openai` | 1.0.0 GA 不需要 milestone 仓库 |
| 2 | 最简调用 | `.prompt().user(msg).call().content()` | ChatClient 是高级 API，不要直接用 ChatModel |
| 3 | Token 统计 | `.call().chatResponse().getMetadata().getUsage()` | promptTokens + completionTokens |
| 4 | System Prompt | `.system("角色设定")` | 让AI按角色回答 |
| 5 | defaultSystem | `ChatClient.builder().defaultSystem("...")` | 推荐方式，不用每次写 .system() |
| 6 | ChatMemory | `MessageWindowChatMemory` + `MessageChatMemoryAdvisor` | 自动维护多轮上下文 |
| 7 | 参数化模板 | `.user(u -> u.text("...{key}...").param("key", val))` | 占位符 {key}，类似 SQL 预编译 |
| 8 | 结构化输出 | `.entity(StudentInfo.class)` | BeanOutputConverter 自动转换 |
| 9 | 多角色管理 | `@Qualifier` 多个 ChatClient Bean | 企业级多角色管理 |
| 10 | RTF 框架 | Role-Task-Format 三要素 | 将提示词理论代码化 |
