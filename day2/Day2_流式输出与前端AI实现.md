# Day2：流式输出 + SSE + 前端AI实现

> **日期**：6月17日 9:00-12:00
> **时长**：3小时（课间休息10分钟）
> **目标**：掌握 Spring AI 流式输出（Flux/SSE），深入理解SSE协议，实现前端AI对话打字机效果
> **版本**：Spring Boot 3.5.15 + Spring AI 1.0.0 GA + JDK 17 + DeepSeek API
> **前置**：Day1 已完成项目搭建、同步调用、ChatMemory、Service分层

---

## Part 1：流式输出基础（9:00-9:45）

### 步骤1：从同步到流式——`.stream()` 替换 `.call()`

> **💡 为什么需要流式输出？**
>
> 昨天我们写的所有 AI 接口都是同步的——用户发消息后，要等 AI 完整生成完所有内容才能看到回复。如果 AI 回复需要 10 秒，用户就盯着空白屏幕等 10 秒。
>
> **同步 vs 流式的体验对比**：
> ```
> 同步模式：用户发送 → [等待10秒...] → 完整回复一次性出现
> 流式模式：用户发送 → 你→好→！→我→是→小→宿→，  （逐字出现，像打字机）
> ```
>
> **类比理解**：同步就像下载电影——必须等整个文件下完才能看；流式就像在线直播——边下边看，不需要等待。
>
> ChatGPT、文心一言、通义千问……所有主流AI产品的回复都是流式的，用户体验远好于同步模式。

在 Day1 的项目中，我们已经有了同步调用的完整代码。现在只需要 **一行改动** 就能实现流式：

```java
// 同步调用（Day1 已完成）
chatClient.prompt().user(message).call().content();    // ← .call()

// 流式调用（Day2 今天学）——只改一个方法！
chatClient.prompt().user(message).stream().content();   // ← .stream()
```

> 💡 **核心要点**：`.call()` 和 `.stream()` 的调用链完全一致，唯一的区别就是最后一个方法名。Spring AI 的设计让同步→流式的切换成本几乎为零。

---

### 步骤2：创建 StreamChatController——第一个流式接口

> **💡 什么是 `TEXT_EVENT_STREAM_VALUE`？**
>
> 这是 HTTP 内容类型 `text/event-stream`，也就是 **SSE（Server-Sent Events）** 协议的 MIME 类型。
> 当 Controller 方法声明了 `produces = TEXT_EVENT_STREAM_VALUE`，就告诉浏览器：
> "我的响应不是一次性返回的，而是一个持续的事件流，你会不断收到数据。"
>
> **类比理解**：普通 HTTP 响应像寄信——写完一整封信再寄出；SSE 像发微信消息——打一个字就发一个字，对方实时看到。

创建 `com.huashan.smart.controller.StreamChatController.java`：

```java
package com.huashan.smart.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * 流式AI对话控制器
 * 演示Flux流式输出的基本用法
 *
 * 和昨天写的 SimpleChatController 的核心区别：
 * - SimpleChatController：.call() → 返回 String（同步，等完整回复）
 * - StreamChatController：.stream() → 返回 Flux<String>（流式，边生成边推送）
 *
 * Flux<String> 是什么？
 * - Flux 是 Reactor 框架的响应式类型，表示"0个或多个元素的异步序列"
 * - Flux<String> 在这里表示"AI的回复被拆成多个String片段，逐个推送"
 * - 每个String片段就是AI生成的一个词或几个字
 * - Spring 自动将 Flux<String> 转为 SSE 格式推送给前端
 */
@RestController
@RequestMapping("/chat")
public class StreamChatController {

    private final ChatClient chatClient;

    /**
     * 构造器注入 ChatClient.Builder
     * 构建一个最基本的 ChatClient（不带角色，不带记忆——纯演示用）
     */
    public StreamChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 【对比接口】同步对话（Day1已经学过，放在这里做对比）
     * GET /chat/sync?message=你好
     *
     * 效果：等AI完整回复后，一次性返回全部文字
     * 体验：可能等5-10秒才看到结果
     */
    @GetMapping("/sync")
    public String chatSync(@RequestParam String message) {
        return chatClient.prompt()   // 开始构建提示词
                .user(message)      // 设置用户消息
                .call()             // ← 同步调用（阻塞等待AI完整回复）
                .content();         // 提取回复文本
    }

    /**
     * 【流式接口】流式对话（今天重点！）
     * GET /chat/stream?message=你好
     *
     * 关键注解解读：
     * @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
     *   → produces = TEXT_EVENT_STREAM_VALUE 声明返回类型是 SSE 事件流
     *   → 告诉浏览器"我的响应是流式的，不要等完整响应，边接收边处理"
     *
     * 返回类型 Flux<String>：
     *   → 不是一次返回完整字符串，而是返回一个"字符串流"
     *   → 每个元素是AI生成的一个文本片段
     *   → Spring 自动将每个元素包装为 SSE 格式：data:文本片段\n\n
     *
     * SSE 格式示例（浏览器实际收到的数据）：
     * data:你
     *
     * data:好
     *
     * data:！
     *
     * data:我是小宿
     *
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestParam String message) {
        return chatClient.prompt()   // 开始构建提示词
                .user(message)      // 设置用户消息
                .stream()           // ← 流式调用（不阻塞，边生成边推送）
                .content();         // 返回 Flux<String>
    }
}
```

> 💡 **代码解析**：
> - 两个接口的唯一区别：`.call()` vs `.stream()`，返回类型 `String` vs `Flux<String>`
> - `produces = MediaType.TEXT_EVENT_STREAM_VALUE` 是必须的，否则 Spring 不知道要转 SSE
> - `Flux<String>` 不需要手动转 SSE——Spring WebFlux 自动处理
> - Spring AI 的 `.stream().content()` 返回的就是 `Flux<String>`，可以直接 return

---

### 步骤3：启动项目 + 浏览器测试流式效果

1. 运行 `SmartApplication.main()`
2. 先测试同步接口（对比用）：

```
http://localhost:8080/chat/sync?message=请介绍高校宿舍管理规定
```

> 等待 5-10 秒后，整个回复一次性出现。

3. 再测试流式接口：

```
http://localhost:8080/chat/stream?message=请介绍高校宿舍管理规定
```

> 文字逐字逐句出现——这就是打字机效果！和 ChatGPT 的体验完全一样。

> 🔧 **常见问题**：
> - 如果浏览器显示的是一整段文字而不是逐字出现：可能是浏览器缓存了完整响应，刷新试试
> - 如果看到 `data:你` `data:好` 这样的原始 SSE 格式：这是正常的，说明 SSE 协议工作正常。在浏览器中看到原始格式，前端用 EventSource 接收后就是逐字效果
> - 流式接口返回后浏览器连接不会立即断开，等 AI 生成完毕后会自动关闭

---

### 步骤4：AiChatServiceImpl 添加 chatStream 方法

> **💡 为什么要给 Service 层添加流式方法？**
>
> 在 Day1 的 Part 4 中，我们建立了完整的三层架构：Controller → Service → ChatClient。但 `AiChatServiceImpl` 只实现了 `chat()` 同步方法。今天要让 Service 层也支持流式，保持架构一致性。
>
> **类比理解**：Service 层就像餐厅的后厨——昨天后厨只会做"堂食"（同步），今天要加"外卖"（流式）能力。前台（Controller）只需要选择"堂食"还是"外卖"，不需要关心后厨怎么做。

修改 `com.huashan.smart.service.AiChatService` 接口，添加 `chatStream` 方法：

```java
package com.huashan.smart.service;

import reactor.core.publisher.Flux;

/**
 * AI聊天Service接口
 * 提供同步对话和流式对话能力
 */
public interface AiChatService {

    /**
     * 同步对话（Day1已实现）
     * @param conversationId 会话ID（用于多轮对话隔离）
     * @param message 用户消息
     * @return AI回复内容
     */
    String chat(String conversationId, String message);

    /**
     * 流式对话（Day2新增）
     * @param conversationId 会话ID
     * @param message 用户消息
     * @return 流式响应（Flux<String> = AI回复的文本片段流）
     *
     * Flux<String> 的含义：
     * - AI的回复不是一次性返回，而是被拆成多个String片段
     * - 每个片段包含AI生成的一个词或几个字
     * - Spring 自动将 Flux<String> 转为 SSE 格式推送
     * - 前端用 EventSource 接收，实现打字机效果
     */
    Flux<String> chatStream(String conversationId, String message);
}
```

修改 `com.huashan.smart.service.impl.AiChatServiceImpl`，添加 `chatStream` 实现：

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
 * 1. 注入 dormitoryChatClient（自带角色+记忆）
 * 2. chat() 方法：同步调用 + 异常处理
 * 3. chatStream() 方法：流式调用 + 异常处理（Day2新增）
 */
@Slf4j
@Service
public class AiChatServiceImpl implements AiChatService {

    private final ChatClient chatClient;

    /**
     * 构造器注入宿舍助手ChatClient
     * 这个ChatClient自带defaultSystem（角色）+ ChatMemory（对话记忆）
     */
    public AiChatServiceImpl(@Qualifier("dormitoryChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 同步对话实现（Day1已完成，此处不再详细注释）
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
     * 流式对话实现（Day2新增）
     *
     * 和 chat() 方法的区别：
     * - .call() → .stream()     同步变流式（只改这一个方法！）
     * - 返回 Flux<String>        代替 String
     *
     * 执行流程：
     * 1. chatClient.prompt()      → 开始构建请求
     * 2. .user(message)           → 设置用户消息
     * 3. .advisors(...)           → 传递conversationId给ChatMemory Advisor
     *    → Advisor前置处理：从Memory取历史消息注入Prompt
     * 4. .stream()                → 流式调用AI模型（不阻塞！）
     *    → AI每生成一个token就推送一个Flux元素
     *    → Advisor后置处理：将本次对话存入Memory
     * 5. .content()               → 返回Flux<String>（每个元素是一个文本片段）
     *
     * ⚠️ 注意：ChatMemory 在流式模式下也能正常工作！
     *    MessageChatMemoryAdvisor 会自动处理流式响应的完整消息保存
     *
     * 为什么用try-catch？
     * - 和同步方法一样，AI API可能超时、密钥过期、服务不可用
     * - Flux.just("错误消息") 创建一个只包含一个元素的流
     * - 相当于同步返回一个错误消息，但包装成了Flux格式（保持接口一致性）
     */
    @Override
    public Flux<String> chatStream(String conversationId, String message) {
        try {
            return chatClient.prompt()
                    .user(message)
                    // 向ChatMemory Advisor传递conversationId
                    .advisors(advisor -> advisor
                            .param("chat_memory_conversation_id", conversationId))
                    .stream()      // ← 关键区别：.stream() 代替 .call()
                    .content();    // 返回 Flux<String>
        } catch (Exception e) {
            log.error("AI流式对话异常：", e);
            // Flux.just() 创建只含一个元素的流（错误提示）
            return Flux.just("AI服务暂时不可用，请稍后再试。");
        }
    }
}
```

> 💡 **核心要点**：
> - 流式方法只需把 `.call()` 改为 `.stream()`，其他调用链完全不变
> - `ChatMemory` 在流式模式下同样有效——Advisor 会自动将完整回复存入 Memory
> - 异常处理用 `Flux.just("错误消息")` 保持返回类型一致性
> - **不需要手动管理线程池**——Flux 基于 Reactor 的异步非阻塞模型，天然支持背压

---

### 步骤5：创建流式端点 GET `/api/aichat/stream`

> **💡 为什么需要单独的流式端点？**
>
> Day1 创建的 `AiChatController` 有两个端点：
> - `POST /api/aichat` → 同步对话，返回 `Result<String>`
> - 今天要添加 `GET /api/aichat/stream` → 流式对话，返回 `Flux<String>`
>
> 为什么流式用 GET 而同步用 POST？
> - SSE（Server-Sent Events）协议要求 GET 请求
> - 浏览器的 `EventSource` API 只能发起 GET 请求
> - 这是协议限制，不是设计选择

修改 `com.huashan.smart.controller.AiChatController`，添加流式端点：

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
 * 两个接口对比：
 * - POST /api/aichat          → 同步对话（等AI回复完再返回，包装在Result中）
 * - GET  /api/aichat/stream   → 流式对话（边生成边推送，直接返回SSE流）
 *
 * ⚠️ 注意流式接口不包装 Result！
 * - 同步接口返回 Result<String>（有 code/message/data）
 * - 流式接口直接返回 Flux<String>（没有 Result 包装）
 * - 为什么？因为 SSE 是持续推送的数据流，无法用 { code, message, data } 包装
 * - 每个 SSE 事件就是一个纯文本片段，前端追加显示即可
 */
@RestController
@RequestMapping("/api/aichat")
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    /**
     * 【接口1】同步对话（Day1已完成）
     * POST /api/aichat
     * 请求体：{ "conversationId": "user001", "message": "今天有多少人缺勤？" }
     */
    @PostMapping
    public Result<String> chat(@RequestBody ChatRequest chatRequest) {
        String reply = aiChatService.chat(
                chatRequest.getConversationId(),
                chatRequest.getMessage()
        );
        return Result.success(reply);
    }

    /**
     * 【接口2】流式对话（Day2新增）
     * GET /api/aichat/stream?conversationId=user001&message=你好
     *
     * 关键注解解读：
     * @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
     *   → produces = TEXT_EVENT_STREAM_VALUE 声明返回类型是 SSE
     *   → 告诉浏览器"我的响应是事件流，不要等完整响应，边接收边处理"
     *
     * 为什么用GET而不是POST？
     * - SSE（Server-Sent Events）协议只支持GET请求
     * - 浏览器的 EventSource API 只能发起GET请求
     * - 如果需要POST方式的流式，需要用 fetch + ReadableStream（更复杂，Part3会讲）
     *
     * 返回类型 Flux<String> 的含义：
     * - 不是一次返回完整字符串，而是返回一个"字符串流"
     * - 每个元素是AI生成的一个文本片段
     * - Spring 自动将每个元素包装为 SSE 格式：data:文本片段\n\n
     *
     * 参数用 @RequestParam 而不是 @RequestBody：
     * - GET 请求没有请求体，参数只能放在 URL 上
     * - conversationId 和 message 通过 ?key=value 传递
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestParam String conversationId,
                                   @RequestParam String message) {
        // 直接返回Flux，Spring自动处理SSE转换
        return aiChatService.chatStream(conversationId, message);
    }

    /**
     * 聊天请求体（同步接口使用）
     */
    @Data
    public static class ChatRequest {
        /** 会话ID */
        private String conversationId = "default";
        /** 用户消息 */
        private String message;
    }
}
```

---

### 步骤6：测试流式端点 + 同步 vs 流式对比

**1. 测试流式端点**

浏览器访问：
```
http://localhost:8080/api/aichat/stream?conversationId=user001&message=讲个笑话
```

观察效果：文字逐字出现，而不是等完整回复。

**2. 对比同步 vs 流式**

用 curl 命令可以更直观地看到区别：

```bash
# 同步接口（等待完整回复后一次性返回）
curl http://localhost:8080/chat/sync?message=你好
# → 等待5秒... → "你好！我是小宿，有什么可以帮你的吗？"

# 流式接口（逐字推送）
curl http://localhost:8080/chat/stream?message=你好
# → 立即开始输出 →
# data:你
# data:好
# data:！
# data:我是小宿
# ...
```

**3. 测试 ChatMemory 在流式模式下是否工作**

```
http://localhost:8080/api/aichat/stream?conversationId=test001&message=我叫张三住301
```

等 AI 回复完，再发第二条：

```
http://localhost:8080/api/aichat/stream?conversationId=test001&message=我住哪
```

> 如果 AI 回复"你住在301宿舍，张三同学"，说明 ChatMemory 在流式模式下正常工作！

> 💡 **同步 vs 流式选择指南**：
> | 场景 | 推荐模式 | 原因 |
> |------|---------|------|
> | 简短问答（<50字） | 同步 | 响应快，实现简单 |
> | 长文生成（>100字） | 流式 | 用户体验好，不用等 |
> | 前端需要打字机效果 | 流式 | 逐字显示 |
> | 后端批处理/定时任务 | 同步 | 不需要实时显示 |
> | 移动端省流量场景 | 同步 | 一次性返回，开销小 |

---

### 步骤7：SseEmitter 方式实现流式（对比方案）

> **💡 为什么还要学 SseEmitter？**
>
> Spring AI 的 `.stream().content()` 返回 `Flux<String>`，这是最简洁的流式方案。但有时候你可能会遇到老项目或特殊需求，需要用 Spring MVC 的 `SseEmitter` 手动实现。
>
> **对比理解 Flux vs SseEmitter**：
>
> | 对比项 | Flux\<String\>（推荐） | SseEmitter（手动） |
> |--------|----------------------|-------------------|
> | 代码量 | 1行：`.stream().content()` | 30+行：手动创建/发送/关闭 |
> | 线程管理 | Reactor自动管理 | 需要手动创建线程池 |
> | 错误处理 | 自动处理 | 需要手动 try-catch |
> | 背压支持 | 天然支持 | 不支持 |
> | 资源释放 | 自动释放 | 需要手动 complete |
> | 适用场景 | Spring AI项目 | 传统Spring MVC项目 |
>
> **类比理解**：Flux 就像自动挡汽车——踩油门就走；SseEmitter 就像手动挡——要自己换挡、踩离合。都能到目的地，但自动挡更轻松。

创建 `com.huashan.smart.controller.SseEmitterChatController.java`（仅作对比演示）：

```java
package com.huashan.smart.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SseEmitter方式实现流式对话（对比演示）
 *
 * ⚠️ 这个Controller仅用于对比教学，实际项目中推荐用Flux方式
 * Flux方式见 StreamChatController 和 AiChatController
 *
 * SseEmitter的工作原理：
 * 1. 创建SseEmitter对象（设置超时时间）
 * 2. 在新线程中调用AI模型获取流式响应
 * 3. 手动遍历Flux，逐个调用emitter.send()发送每个片段
 * 4. 发送完毕后调用emitter.complete()关闭连接
 *
 * 问题：为什么不能直接return Flux？
 * 因为SseEmitter是Spring MVC的同步模型，不支持直接返回Flux
 * 需要手动在新线程中消费Flux，然后通过emitter.send推送
 */
@Slf4j
@RestController
@RequestMapping("/sse-emitter")
public class SseEmitterChatController {

    private final ChatClient chatClient;

    /** SSE线程池：用于在新线程中处理流式响应 */
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public SseEmitterChatController(@Qualifier("dormitoryChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * SseEmitter方式流式对话
     * GET /sse-emitter/chat?message=你好
     *
     * 对比 Flux 方式的代码量差异：
     * Flux: return chatClient.prompt().user(message).stream().content();  // 1行
     * SseEmitter: 需要创建对象 → 开线程 → 遍历Flux → send → complete  // 30+行
     */
    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatStream(@RequestParam String message) {
        // 1. 创建SseEmitter，设置60秒超时
        SseEmitter emitter = new SseEmitter(60000L);

        // 2. 在新线程中处理流式响应（不能在主线程，否则会阻塞）
        executorService.execute(() -> {
            try {
                // 3. 调用Spring AI获取Flux流
                Flux<String> flux = chatClient.prompt()
                        .user(message)
                        .stream()
                        .content();

                // 4. 订阅Flux，逐个发送每个文本片段
                flux.doOnNext(chunk -> {
                            // 每收到一个文本片段，就通过emitter发送给前端
                            try {
                                emitter.send(SseEmitter.event().data(chunk));
                            } catch (IOException e) {
                                log.error("SSE发送chunk失败：", e);
                            }
                        })
                        .doOnComplete(() -> {
                            // 5. AI生成完毕，关闭SSE连接
                            try {
                                emitter.send(SseEmitter.event().data("[DONE]"));
                                emitter.complete();
                            } catch (IOException e) {
                                log.error("SSE完成信号发送失败：", e);
                            }
                        })
                        .doOnError(emitter::completeWithError)  // 6. 出错时关闭连接
                        .subscribe();  // 7. 订阅Flux（触发实际执行）
            } catch (Exception e) {
                log.error("SseEmitter流式对话异常：", e);
                try {
                    emitter.send(SseEmitter.event().data("AI服务异常"));
                    emitter.complete();
                } catch (IOException ex) {
                    log.error("SSE异常通知发送失败：", ex);
                }
            }
        });

        // 8. 立即返回emitter（不等AI回复完）
        return emitter;
    }
}
```

> 💡 **对比结论**：
> - Flux 方式：**1行代码**完成流式输出——`.stream().content()` 搞定
> - SseEmitter 方式：**30+行代码**，需要手动创建线程、遍历Flux、send、complete、处理异常
> - 在 Spring AI 1.0.0 GA 项目中，**强烈推荐 Flux 方式**
> - SseEmitter 了解即可，遇到老项目迁移时可能需要

---

## Part 2：SSE深入（9:45-10:30）

### 步骤8：SSE 消息格式详解

> **💡 SSE（Server-Sent Events）协议是什么？**
>
> SSE 是 HTML5 标准定义的服务器推送协议。它基于 HTTP，让服务器可以主动向浏览器推送数据。
>
> **类比理解**：
> - 普通 HTTP：像打电话——你说一句，对方回一句，说完就挂
> - WebSocket：像打电话——双方可以同时说话（全双工）
> - SSE：像听收音机——你调到频道后，电台持续播放内容（服务器单向推送）
>
> **SSE vs WebSocket 选择指南**：
> | 对比项 | SSE | WebSocket |
> |--------|-----|-----------|
> | 通信方向 | 服务器→客户端（单向） | 双向 |
> | 协议 | HTTP | WS |
> | 断线重连 | 浏览器自动重连 | 需要手动实现 |
> | 浏览器API | EventSource（简单） | WebSocket（复杂） |
> | 适用场景 | AI回复推送、通知、日志流 | 聊天室、协同编辑、游戏 |
>
> **AI对话用 SSE 就够了**——因为只有服务器往客户端推数据，不需要客户端主动推。

**SSE 消息格式标准**：

```
field:value\n\n
```

SSE 支持四个字段：

| 字段 | 作用 | 示例 |
|------|------|------|
| `data` | 消息数据（必须） | `data:你好\n\n` |
| `event` | 事件类型（可选） | `event:progress\n\ndata:50%\n\n` |
| `id` | 事件ID（可选，用于断线重连） | `id:42\n\ndata:hello\n\n` |
| `retry` | 重连间隔毫秒（可选） | `retry:3000\n\n` |

**SSE 消息格式示例**：

```
id:1
event:message
data:你

id:2
event:message
data:好

id:3
event:done
data:[DONE]
```

> 💡 **空行 `\n\n` 是消息分隔符**：SSE 协议规定，两个 `\n`（一个空行）表示一条消息的结束。每条消息由多个 `field:value` 行组成，消息之间用空行分隔。

---

### 步骤9：ServerSentEvent 自定义事件类型

> **💡 为什么要自定义事件类型？**
>
> 默认的 SSE 只有一种 `message` 事件类型。前端用 `EventSource.onmessage` 接收所有数据。
> 但在实际场景中，你可能需要区分不同类型的事件：
> - AI 回复文本 → `event:content`
> - 进度更新 → `event:progress`
> - 错误通知 → `event:error`
> - 完成信号 → `event:done`
>
> 前端可以用 `addEventListener("progress", ...)` 只监听特定类型的事件，互不干扰。
>
> **类比理解**：就像微信的消息分类——文字消息、语音消息、图片消息分别显示在不同的区域。SSE 的事件类型就是"消息分类"。

Spring 提供了 `ServerSentEvent` 包装类，可以为每个 SSE 事件指定 `event`、`id`、`data`：

```java
// 不使用 ServerSentEvent（默认 event=message）
@GetMapping(value = "/simple", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> simple() {
    return chatClient.prompt().user("你好").stream().content();
}
// 输出：data:你好\n\n   （默认event=message）

// 使用 ServerSentEvent（自定义 event 类型）
@GetMapping(value = "/typed", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent<String>> typed() {
    return chatClient.prompt().user("你好").stream().content()
            .map(chunk -> ServerSentEvent.<String>builder()
                    .event("content")     // 自定义事件类型
                    .data(chunk)          // 消息数据
                    .build());
}
// 输出：event:content\ndata:你好\n\n  （event类型是content）
```

---

### 步骤10：创建 SSEChatController——报修进度分阶段推送

> **💡 业务场景**：学生在宿舍管理系统提交报修单后，想知道报修进度。我们可以用 SSE 模拟报修进度分阶段推送：
> 1. `event:submitted` → 已提交
> 2. `event:assigned` → 已派单
> 3. `event:repairing` → 修理中
> 4. `event:completed` → 已完成
>
> 每个阶段使用不同的 `event` 类型，前端可以分别处理。

创建 `com.huashan.smart.controller.SSEChatController.java`：

```java
package com.huashan.smart.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.UUID;

/**
 * SSE高级演示控制器
 * 演示ServerSentEvent自定义事件类型 + 报修进度分阶段推送
 *
 * 本Controller演示两个SSE高级用法：
 * 1. ServerSentEvent 包装：自定义 event 类型、id、data
 * 2. delayElements 延时推送：模拟实时进度更新
 *
 * 业务场景：学生报修后，实时推送报修进度
 * - event:submitted  → 报修单已提交
 * - event:assigned   → 已派维修工
 * - event:repairing  → 正在维修
 * - event:completed  → 维修完成
 */
@Slf4j
@RestController
@RequestMapping("/api/sse")
public class SSEChatController {

    /**
     * 报修进度推送（SSE自定义事件类型）
     * GET /api/sse/repair-progress?repairId=R001
     *
     * 执行流程：
     * 1. 生成报修单号
     * 2. 每2秒推送一个阶段（用delayElements延时）
     * 3. 每个阶段使用不同的event类型
     * 4. 前端用addEventListener按事件类型分别处理
     *
     * ServerSentEvent.builder() 参数解读：
     * - .event("xxx")    → 设置SSE事件类型（前端用addEventListener("xxx")监听）
     * - .id("123")       → 设置事件ID（用于断线重连时，服务器从id之后继续推送）
     * - .data("内容")     → 设置消息数据
     */
    @GetMapping(value = "/repair-progress", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> repairProgress(@RequestParam(defaultValue = "R001") String repairId) {

        // 阶段1：已提交
        ServerSentEvent<String> submitted = ServerSentEvent.<String>builder()
                .event("submitted")                              // 事件类型：已提交
                .id("1")                                         // 事件ID（用于断线重连）
                .data("报修单 " + repairId + " 已提交成功")       // 消息内容
                .build();

        // 阶段2：已派单
        ServerSentEvent<String> assigned = ServerSentEvent.<String>builder()
                .event("assigned")                               // 事件类型：已派单
                .id("2")
                .data("维修工李师傅已接单，预计30分钟到达")
                .build();

        // 阶段3：修理中
        ServerSentEvent<String> repairing = ServerSentEvent.<String>builder()
                .event("repairing")                              // 事件类型：修理中
                .id("3")
                .data("李师傅正在维修，请稍候...")
                .build();

        // 阶段4：已完成
        ServerSentEvent<String> completed = ServerSentEvent.<String>builder()
                .event("completed")                              // 事件类型：已完成
                .id("4")
                .data("维修完成！感谢您的耐心等待。")
                .build();

        // 使用Flux.just()创建包含4个阶段的流
        // .delayElements(Duration.ofSeconds(2))：每个元素间隔2秒推送
        // 效果：每2秒推送一个阶段的进度
        return Flux.just(submitted, assigned, repairing, completed)
                .delayElements(Duration.ofSeconds(2));  // 每个阶段间隔2秒
    }

    /**
     * AI流式对话（带自定义事件类型）
     * GET /api/sse/ai-chat?message=你好
     *
     * 和普通 Flux<String> 流式接口的区别：
     * - 普通方式：所有数据都用默认的 message 事件类型
     * - 本接口：AI文本用 content 事件类型，完成信号用 done 事件类型
     *
     * 这样前端可以：
     * - addEventListener("content", ...) → 处理AI回复文本
     * - addEventListener("done", ...)    → 知道AI回复完毕，关闭连接
     */
    @GetMapping(value = "/ai-chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> aiChat(@RequestParam String message) {
        // 这里暂用最基础的ChatClient（演示SSE事件类型用法）
        // 实际项目中注入dormitoryChatClient即可
        return null; // 占位，步骤11会实现完整的带ChatMemory版本
    }
}
```

---

### 步骤11：delayElements 延时推送 + 测试 SSE 进度

> **💡 什么是 `delayElements`？**
>
> `delayElements(Duration.ofSeconds(2))` 是 Reactor 提供的延时操作符，让 Flux 中的每个元素在推送前等待指定的时间。
>
> **类比理解**：就像设置闹钟——你设置了4个闹钟，每个间隔2小时。到时间了闹钟才响，不会一起响。

**测试报修进度推送**：

浏览器访问：
```
http://localhost:8080/api/sse/repair-progress?repairId=R001
```

观察效果：每隔2秒出现一条新的进度消息，4个阶段共8秒。

curl 测试（可以看到完整的 SSE 格式）：
```bash
curl http://localhost:8080/api/sse/repair-progress?repairId=R001
```

输出：
```
event:submitted
id:1
data:报修单 R001 已提交成功

event:assigned
id:2
data:维修工李师傅已接单，预计30分钟到达

event:repairing
id:3
data:李师傅正在维修，请稍候...

event:completed
id:4
data:维修完成！感谢您的耐心等待。
```

> 💡 每条消息之间有2秒间隔（`delayElements` 的效果），模拟真实的报修进度更新。

---

### 步骤12：前端 EventSource 接收 + addEventListener 按事件类型监听

> **💡 前端如何接收 SSE 数据？**
>
> 浏览器内置了 `EventSource` API，专门用于接收 SSE 数据。
>
> **两种监听方式**：
> ```javascript
> // 方式1：默认监听（只接收 event=message 的数据）
> const es = new EventSource("/api/sse/repair-progress?repairId=R001");
> es.onmessage = (e) => { console.log(e.data); };
>
> // 方式2：按事件类型监听（推荐！可以区分不同类型的事件）
> const es = new EventSource("/api/sse/repair-progress?repairId=R001");
> es.addEventListener("submitted", (e) => { console.log("已提交:", e.data); });
> es.addEventListener("assigned",  (e) => { console.log("已派单:", e.data); });
> es.addEventListener("repairing", (e) => { console.log("修理中:", e.data); });
> es.addEventListener("completed", (e) => { console.log("已完成:", e.data); });
> ```
>
> **类比理解**：`onmessage` 像收音机——所有频道混在一起听；`addEventListener` 像电视——你可以选频道看不同的节目。

---

### 步骤13：创建 sse-test.html 完整测试页面 + 断线重连

> **💡 为什么需要断线重连？**
>
> 网络不稳定时，SSE 连接可能断开。浏览器内置的 `EventSource` 会自动重连（发送 `Last-Event-ID` 请求头），但如果用 `fetch` 方式则需要手动实现重连逻辑。

在 `src/main/resources/static/` 目录下创建 `sse-test.html`：

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SSE 流式测试页面</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            background: #f5f7fa;
            padding: 20px;
            max-width: 900px;
            margin: 0 auto;
        }
        h1 { color: #333; margin-bottom: 20px; font-size: 22px; }
        h2 { color: #555; margin: 20px 0 10px; font-size: 18px; }

        /* 通用卡片样式 */
        .card {
            background: #fff;
            border-radius: 8px;
            padding: 16px;
            margin-bottom: 16px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }

        /* 输入区域 */
        .input-group {
            display: flex;
            gap: 8px;
            margin-bottom: 10px;
        }
        input[type="text"] {
            flex: 1;
            padding: 8px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }
        button {
            padding: 8px 16px;
            background: #409eff;
            color: #fff;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            white-space: nowrap;
        }
        button:hover { background: #66b1ff; }
        button:disabled { background: #c0c4cc; cursor: not-allowed; }
        button.danger { background: #f56c6c; }
        button.danger:hover { background: #f78989; }

        /* 输出区域 */
        .output {
            background: #1e1e1e;
            color: #d4d4d4;
            padding: 12px;
            border-radius: 4px;
            font-family: "Cascadia Code", "Fira Code", Consolas, monospace;
            font-size: 13px;
            line-height: 1.6;
            min-height: 120px;
            max-height: 300px;
            overflow-y: auto;
            white-space: pre-wrap;
            word-break: break-all;
        }

        /* 报修进度样式 */
        .progress-list { list-style: none; padding: 0; }
        .progress-item {
            display: flex;
            align-items: flex-start;
            padding: 8px 0;
            border-bottom: 1px solid #f0f0f0;
        }
        .progress-item:last-child { border-bottom: none; }
        .progress-dot {
            width: 12px;
            height: 12px;
            border-radius: 50%;
            background: #e0e0e0;
            margin-right: 10px;
            margin-top: 4px;
            flex-shrink: 0;
        }
        .progress-dot.active { background: #67c23a; animation: pulse 1s infinite; }
        .progress-dot.done { background: #67c23a; }
        .progress-text { flex: 1; }
        .progress-event {
            font-size: 11px;
            color: #999;
            background: #f5f5f5;
            padding: 1px 6px;
            border-radius: 3px;
            margin-left: 8px;
        }

        /* 状态指示 */
        .status {
            display: inline-block;
            padding: 2px 8px;
            border-radius: 3px;
            font-size: 12px;
            margin-bottom: 8px;
        }
        .status.connected { background: #e1f3d8; color: #67c23a; }
        .status.disconnected { background: #fde2e2; color: #f56c6c; }
        .status.connecting { background: #faecd8; color: #e6a23c; }

        @keyframes pulse {
            0%, 100% { opacity: 1; }
            50% { opacity: 0.5; }
        }
    </style>
</head>
<body>
    <h1>SSE 流式输出测试页面</h1>

    <!-- ========== 模块1：AI流式对话 ========== -->
    <div class="card">
        <h2>1. AI 流式对话</h2>
        <div class="input-group">
            <input type="text" id="aiMessage" placeholder="输入消息，如：介绍一下宿舍管理规定" value="介绍一下宿舍管理规定">
            <button id="aiSendBtn" onclick="startAiChat()">发送</button>
            <button class="danger" onclick="stopAiChat()">停止</button>
        </div>
        <div id="aiStatus" class="status disconnected">未连接</div>
        <div class="output" id="aiOutput">等待发送...</div>
    </div>

    <!-- ========== 模块2：报修进度推送 ========== -->
    <div class="card">
        <h2>2. 报修进度推送（自定义事件类型）</h2>
        <div class="input-group">
            <input type="text" id="repairId" placeholder="报修单号" value="R001">
            <button onclick="startRepairProgress()">查询进度</button>
            <button class="danger" onclick="stopRepairProgress()">停止</button>
        </div>
        <div id="repairStatus" class="status disconnected">未连接</div>
        <ul class="progress-list" id="progressList">
            <li class="progress-item">
                <div class="progress-dot" id="dot-submitted"></div>
                <div class="progress-text" id="text-submitted">等待提交...</div>
                <span class="progress-event">submitted</span>
            </li>
            <li class="progress-item">
                <div class="progress-dot" id="dot-assigned"></div>
                <div class="progress-text" id="text-assigned">等待派单...</div>
                <span class="progress-event">assigned</span>
            </li>
            <li class="progress-item">
                <div class="progress-dot" id="dot-repairing"></div>
                <div class="progress-text" id="text-repairing">等待维修...</div>
                <span class="progress-event">repairing</span>
            </li>
            <li class="progress-item">
                <div class="progress-dot" id="dot-completed"></div>
                <div class="progress-text" id="text-completed">等待完成...</div>
                <span class="progress-event">completed</span>
            </li>
        </ul>
    </div>

    <!-- ========== 模块3：同步 vs 流式对比 ========== -->
    <div class="card">
        <h2>3. 同步 vs 流式对比</h2>
        <div class="input-group">
            <input type="text" id="compareMsg" placeholder="输入对比消息" value="讲个笑话">
            <button onclick="compareSync()">同步调用</button>
            <button onclick="compareStream()">流式调用</button>
        </div>
        <div style="display: flex; gap: 12px;">
            <div style="flex: 1;">
                <div style="font-size: 12px; color: #999; margin-bottom: 4px;">同步（等完整回复）</div>
                <div class="output" id="syncOutput" style="min-height: 80px;">等待发送...</div>
            </div>
            <div style="flex: 1;">
                <div style="font-size: 12px; color: #999; margin-bottom: 4px;">流式（逐字推送）</div>
                <div class="output" id="streamOutput" style="min-height: 80px;">等待发送...</div>
            </div>
        </div>
    </div>

    <script>
        // ==================== 模块1：AI流式对话 ====================

        let aiEventSource = null;  // EventSource实例

        /**
         * 开始AI流式对话
         *
         * EventSource 工作流程：
         * 1. new EventSource(url)  → 创建SSE连接（浏览器自动发起GET请求）
         * 2. onmessage 回调         → 每收到一条SSE消息就触发
         * 3. onerror 回调           → 连接出错时触发
         * 4. es.close()             → 手动关闭连接
         */
        function startAiChat() {
            const message = document.getElementById('aiMessage').value;
            if (!message) return alert('请输入消息');

            // 关闭之前的连接
            if (aiEventSource) aiEventSource.close();

            // 清空输出
            const output = document.getElementById('aiOutput');
            output.textContent = '';

            // 更新状态
            updateStatus('aiStatus', 'connecting', '连接中...');

            // 创建EventSource连接
            // SSE默认的event类型是message，所以用onmessage接收
            const url = `/api/aichat/stream?conversationId=web-test&message=${encodeURIComponent(message)}`;
            aiEventSource = new EventSource(url);

            // 每收到一条SSE消息就追加显示
            aiEventSource.onmessage = function(event) {
                output.textContent += event.data;  // 逐字追加（打字机效果的核心！）
                output.scrollTop = output.scrollHeight;  // 自动滚到底部
                updateStatus('aiStatus', 'connected', '接收中');
            };

            // 连接出错
            aiEventSource.onerror = function() {
                if (aiEventSource.readyState === EventSource.CLOSED) {
                    updateStatus('aiStatus', 'disconnected', '已完成');
                } else {
                    updateStatus('aiStatus', 'disconnected', '连接断开');
                    // EventSource 会自动重连！不需要手动处理
                    // 如果不想自动重连，调用 es.close()
                }
            };
        }

        /** 停止AI对话 */
        function stopAiChat() {
            if (aiEventSource) {
                aiEventSource.close();  // 关闭SSE连接
                aiEventSource = null;
                updateStatus('aiStatus', 'disconnected', '已停止');
            }
        }

        // ==================== 模块2：报修进度推送 ====================

        let repairEventSource = null;

        /**
         * 开始监听报修进度
         *
         * 关键：使用 addEventListener 按事件类型分别监听！
         * - addEventListener("submitted", ...)  → 只接收 submitted 事件
         * - addEventListener("assigned", ...)   → 只接收 assigned 事件
         * - 互不干扰，前端可以针对不同事件类型做不同处理
         */
        function startRepairProgress() {
            const repairId = document.getElementById('repairId').value || 'R001';

            if (repairEventSource) repairEventSource.close();

            // 重置进度显示
            ['submitted', 'assigned', 'repairing', 'completed'].forEach(type => {
                document.getElementById('dot-' + type).className = 'progress-dot';
            });
            document.getElementById('text-submitted').textContent = '等待提交...';
            document.getElementById('text-assigned').textContent = '等待派单...';
            document.getElementById('text-repairing').textContent = '等待维修...';
            document.getElementById('text-completed').textContent = '等待完成...';

            updateStatus('repairStatus', 'connecting', '连接中...');

            const url = `/api/sse/repair-progress?repairId=${encodeURIComponent(repairId)}`;
            repairEventSource = new EventSource(url);

            // ⭐ 核心代码：按事件类型分别监听！
            // 每种事件类型对应不同的UI更新逻辑
            repairEventSource.addEventListener("submitted", function(event) {
                document.getElementById('dot-submitted').className = 'progress-dot done';
                document.getElementById('text-submitted').textContent = event.data;
                updateStatus('repairStatus', 'connected', '接收中');
            });

            repairEventSource.addEventListener("assigned", function(event) {
                document.getElementById('dot-assigned').className = 'progress-dot done';
                document.getElementById('text-assigned').textContent = event.data;
            });

            repairEventSource.addEventListener("repairing", function(event) {
                document.getElementById('dot-repairing').className = 'progress-dot active';
                document.getElementById('text-repairing').textContent = event.data;
            });

            repairEventSource.addEventListener("completed", function(event) {
                document.getElementById('dot-repairing').className = 'progress-dot done';
                document.getElementById('dot-completed').className = 'progress-dot done';
                document.getElementById('text-completed').textContent = event.data;
                // 完成后关闭连接
                repairEventSource.close();
                repairEventSource = null;
                updateStatus('repairStatus', 'disconnected', '已完成');
            });

            repairEventSource.onerror = function() {
                if (repairEventSource.readyState === EventSource.CLOSED) {
                    updateStatus('repairStatus', 'disconnected', '已完成');
                }
            };
        }

        /** 停止监听报修进度 */
        function stopRepairProgress() {
            if (repairEventSource) {
                repairEventSource.close();
                repairEventSource = null;
                updateStatus('repairStatus', 'disconnected', '已停止');
            }
        }

        // ==================== 模块3：同步 vs 流式对比 ====================

        /** 同步调用（用fetch，等待完整响应） */
        async function compareSync() {
            const message = document.getElementById('compareMsg').value;
            const output = document.getElementById('syncOutput');
            output.textContent = '等待中...（同步模式需要等AI完整回复）';

            const startTime = Date.now();

            try {
                const response = await fetch('/api/aichat', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ conversationId: 'compare-sync', message: message })
                });
                const result = await response.json();
                const elapsed = ((Date.now() - startTime) / 1000).toFixed(1);
                output.textContent = `[等待${elapsed}秒后一次性返回]\n\n${result.data}`;
            } catch (e) {
                output.textContent = '请求失败: ' + e.message;
            }
        }

        /** 流式调用（用EventSource，逐字显示） */
        function compareStream() {
            const message = document.getElementById('compareMsg').value;
            const output = document.getElementById('streamOutput');
            output.textContent = '';

            const startTime = Date.now();
            let firstChunk = true;

            const url = `/api/aichat/stream?conversationId=compare-stream&message=${encodeURIComponent(message)}`;
            const es = new EventSource(url);

            es.onmessage = function(event) {
                if (firstChunk) {
                    const elapsed = ((Date.now() - startTime) / 1000).toFixed(1);
                    output.textContent = `[${elapsed}秒后开始输出]\n\n`;
                    firstChunk = false;
                }
                output.textContent += event.data;
                output.scrollTop = output.scrollHeight;
            };

            es.onerror = function() {
                es.close();
            };
        }

        // ==================== 工具函数 ====================

        /** 更新状态指示器 */
        function updateStatus(elementId, status, text) {
            const el = document.getElementById(elementId);
            el.className = 'status ' + status;
            el.textContent = text;
        }
    </script>
</body>
</html>
```

> 💡 **sse-test.html 三个测试模块**：
> 1. **AI流式对话**：输入消息，用 EventSource 接收流式回复，逐字追加显示
> 2. **报修进度推送**：用 `addEventListener` 按事件类型监听不同阶段的进度更新
> 3. **同步 vs 流式对比**：同一个问题，左侧同步等待完整回复，右侧流式逐字显示，直观对比体验差异
>
> **断线重连**：`EventSource` 内置自动重连机制。连接断开后，浏览器会自动重新连接。如果服务器在 SSE 消息中设置了 `id` 字段，重连时会发送 `Last-Event-ID` 请求头，服务器可以据此从断点续传。

---

## 课间休息（10:30-10:40）

---

## Part 3：前端AI对话实现（10:40-11:30）

### 步骤14：改造 AiChat.vue——添加流式模式切换

> **💡 为什么要改造 AiChat.vue？**
>
> Day1 的 AiChat.vue 只支持同步模式——发送消息后等 AI 完整回复。今天要添加流式模式，让用户可以切换"同步"和"流式"两种对话方式。
>
> **改造思路**：
> 1. 添加模式切换按钮（同步/流式）
> 2. 流式模式用 `EventSource` 接收数据
> 3. 同步模式用 `fetch` 接收数据
> 4. 两种模式共用同一个消息列表

> **💡 Vue3 + Element Plus 项目结构**：
> AiChat.vue 是 Vue3 单文件组件（SFC），使用 Element Plus 的 UI 组件。
> 以下代码假设项目使用 Vue3 + Element Plus + Vite。

创建/修改 `AiChat.vue`：

```vue
<template>
  <div class="ai-chat">
    <!-- 顶部标题栏 + 模式切换 -->
    <div class="chat-header">
      <h3>AI宿舍助手 - 小宿</h3>
      <!-- 模式切换：同步/流式 -->
      <div class="mode-switch">
        <span>对话模式：</span>
        <!-- el-radio-group：Element Plus的单选按钮组 -->
        <!-- v-model="chatMode"：双向绑定，选中值自动同步到chatMode变量 -->
        <el-radio-group v-model="chatMode" size="small">
          <el-radio-button value="sync">同步</el-radio-button>
          <el-radio-button value="stream">流式</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 聊天消息区域 -->
    <div class="chat-messages" ref="messagesContainer">
      <!-- 遍历消息列表，渲染每条消息 -->
      <!-- :class动态绑定样式——用户消息靠右，AI消息靠左 -->
      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="['message', msg.role === 'user' ? 'user-message' : 'ai-message']"
      >
        <!-- 消息角色标签 -->
        <div class="message-role">{{ msg.role === 'user' ? '我' : '小宿' }}</div>
        <!-- 消息内容 -->
        <!-- AI消息使用typing-content类，支持打字机CSS动画 -->
        <div :class="['message-content', msg.isStreaming ? 'typing-content' : '']">
          {{ msg.content }}
          <!-- 流式输出时的闪烁光标 -->
          <span v-if="msg.isStreaming" class="cursor-blink">|</span>
        </div>
      </div>

      <!-- 空状态提示 -->
      <div v-if="messages.length === 0" class="empty-state">
        你好！我是宿舍助手"小宿"，有什么可以帮你的吗？
      </div>
    </div>

    <!-- 底部输入区域 -->
    <div class="chat-input">
      <!-- el-input：Element Plus输入框 -->
      <!-- @keyup.enter：按回车发送 -->
      <!-- :disabled：发送中禁用输入 -->
      <el-input
        v-model="inputMessage"
        placeholder="输入消息..."
        @keyup.enter="sendMessage"
        :disabled="isLoading"
        size="large"
      >
        <template #append>
          <!-- 发送按钮 -->
          <el-button
            @click="sendMessage"
            :loading="isLoading"
            type="primary"
          >
            发送
          </el-button>
        </template>
      </el-input>
    </div>
  </div>
</template>

<script setup>
/**
 * AiChat.vue - AI对话组件
 *
 * 支持两种对话模式：
 * 1. 同步模式（sync）：等AI完整回复后一次性显示
 * 2. 流式模式（stream）：AI逐字推送，打字机效果
 *
 * 核心数据结构：
 * - chatMode：当前模式（'sync' 或 'stream'）
 * - messages：聊天消息列表（每条消息包含role、content、isStreaming）
 * - inputMessage：当前输入的消息
 * - isLoading：是否正在等待AI回复
 * - eventSource：EventSource实例（流式模式使用）
 */

import { ref, nextTick } from 'vue'

// ==================== 响应式数据 ====================

/** 对话模式：sync=同步，stream=流式 */
const chatMode = ref('stream')

/** 聊天消息列表 */
const messages = ref([])

/** 当前输入的消息文本 */
const inputMessage = ref('')

/** 是否正在加载（等待AI回复） */
const isLoading = ref(false)

/** EventSource实例（流式模式使用） */
let eventSource = null

/** 当前会话ID（用于ChatMemory多轮对话隔离） */
const conversationId = ref('user-' + Date.now())

/** 消息容器DOM引用（用于自动滚动到底部） */
const messagesContainer = ref(null)

// ==================== 方法 ====================

/**
 * 发送消息（统一入口）
 * 根据chatMode选择同步或流式发送
 */
function sendMessage() {
  const message = inputMessage.value.trim()
  if (!message || isLoading.value) return

  // 添加用户消息到消息列表
  messages.value.push({
    role: 'user',        // 角色：user=用户
    content: message,    // 消息内容
    isStreaming: false   // 是否正在流式输出
  })

  // 清空输入框
  inputMessage.value = ''
  isLoading.value = true

  // 根据模式选择发送方式
  if (chatMode.value === 'stream') {
    sendStreamMessage(message)   // 流式发送
  } else {
    sendSyncMessage(message)      // 同步发送
  }

  // 滚动到底部
  scrollToBottom()
}

/**
 * 同步发送消息
 * 使用fetch发送POST请求，等待完整响应
 *
 * 流程：
 * 1. fetch POST → 发送请求
 * 2. 等待AI完整回复
 * 3. 将回复添加到消息列表
 */
async function sendSyncMessage(message) {
  try {
    // 发送同步请求
    const response = await fetch('/api/aichat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        conversationId: conversationId.value,
        message: message
      })
    })

    // 解析响应JSON
    const result = await response.json()

    // 添加AI回复到消息列表
    if (result.code === 200) {
      messages.value.push({
        role: 'assistant',     // 角色：assistant=AI
        content: result.data,  // AI回复内容
        isStreaming: false
      })
    } else {
      // 接口返回错误
      messages.value.push({
        role: 'assistant',
        content: '抱歉，出了点问题：' + result.message,
        isStreaming: false
      })
    }
  } catch (error) {
    // 网络错误
    messages.value.push({
      role: 'assistant',
      content: '网络错误，请检查网络连接后重试。',
      isStreaming: false
    })
  } finally {
    isLoading.value = false
    scrollToBottom()
  }
}

/**
 * 流式发送消息
 * 使用EventSource接收SSE流式数据
 *
 * ⭐ 这是Day2的核心代码！
 *
 * 流程：
 * 1. 添加一条空的AI消息到列表（isStreaming=true，显示闪烁光标）
 * 2. 创建EventSource连接
 * 3. onmessage：每收到一个文本片段，追加到AI消息的content中
 * 4. onerror：连接结束，设置isStreaming=false（隐藏光标）
 */
function sendStreamMessage(message) {
  // 1. 添加一条空的AI消息（占位，后续逐字追加内容）
  const aiMessage = {
    role: 'assistant',
    content: '',           // 初始为空
    isStreaming: true      // 标记正在流式输出（显示闪烁光标）
  }
  messages.value.push(aiMessage)

  // 2. 创建EventSource连接
  const url = `/api/aichat/stream?conversationId=${encodeURIComponent(conversationId.value)}&message=${encodeURIComponent(message)}`
  eventSource = new EventSource(url)

  // 3. 每收到一条SSE消息，追加到AI消息的content中
  eventSource.onmessage = function(event) {
    // ⭐ 打字机效果的核心：逐字追加！
    aiMessage.content += event.data
    scrollToBottom()
  }

  // 4. 连接结束或出错
  eventSource.onerror = function() {
    // 关闭EventSource
    if (eventSource) {
      eventSource.close()
      eventSource = null
    }
    // 标记流式输出结束（隐藏闪烁光标）
    aiMessage.isStreaming = false
    isLoading.value = false
  }
}

/** 滚动到底部 */
function scrollToBottom() {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}
</script>

<style scoped>
/* 整体布局 */
.ai-chat {
  display: flex;
  flex-direction: column;
  height: 600px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
}

/* 顶部标题栏 */
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #409eff;
  color: #fff;
}
.chat-header h3 { margin: 0; font-size: 16px; }
.mode-switch { display: flex; align-items: center; gap: 8px; font-size: 13px; }

/* 消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f5f7fa;
}

/* 单条消息 */
.message {
  margin-bottom: 16px;
  max-width: 80%;
}
.user-message { margin-left: auto; }
.ai-message { margin-right: auto; }

/* 消息角色 */
.message-role {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

/* 消息内容气泡 */
.message-content {
  padding: 10px 14px;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}
.user-message .message-content {
  background: #409eff;
  color: #fff;
  border-bottom-right-radius: 2px;
}
.ai-message .message-content {
  background: #fff;
  color: #333;
  border: 1px solid #e4e7ed;
  border-bottom-left-radius: 2px;
}

/* 输入区域 */
.chat-input {
  padding: 12px;
  border-top: 1px solid #e4e7ed;
}

/* 空状态 */
.empty-state {
  text-align: center;
  color: #999;
  padding: 60px 20px;
  font-size: 14px;
}
</style>
```

---

### 步骤15：EventSource 接收流式数据 + 逐字追加显示

> **💡 打字机效果的核心原理**：
>
> 打字机效果只需要两行关键代码：
> ```javascript
> eventSource.onmessage = function(event) {
>     aiMessage.content += event.data;  // ← 核心：逐字追加
> }
> ```
>
> **为什么能实现逐字效果？**
> 1. 服务端 `Flux<String>` 每个 token 推送一个 SSE 事件
> 2. 浏览器 `EventSource.onmessage` 每收到一个事件就触发回调
> 3. 回调中 `content += event.data` 把新文本追加到现有文本后面
> 4. Vue 的响应式系统自动检测 `content` 变化，更新 DOM
> 5. 用户看到的就是文字逐字出现——打字机效果！
>
> **数据流转全链路**：
> ```
> AI模型生成token → Spring AI Flux推送 → SSE格式 data:xxx
> → 浏览器EventSource接收 → onmessage回调 → content += data
> → Vue响应式更新DOM → 用户看到打字机效果
> ```

---

### 步骤16：聊天记录管理——前端维护消息列表

> **💡 前端消息列表的设计**：
>
> ```javascript
> messages.value = [
>   { role: 'user',      content: '你好',              isStreaming: false },
>   { role: 'assistant', content: '你好！我是小宿...',  isStreaming: false },
>   { role: 'user',      content: '我住哪',             isStreaming: false },
>   { role: 'assistant', content: '你住在301宿舍。',     isStreaming: false },
>   { role: 'assistant', content: '你',                 isStreaming: true  },  // ← 正在流式输出
> ]
> ```
>
> **每条消息的三个字段**：
> - `role`：角色（`user`=用户，`assistant`=AI）
> - `content`：消息文本内容
> - `isStreaming`：是否正在流式输出（控制闪烁光标的显示/隐藏）
>
> **类比理解**：消息列表就像微信的聊天记录——每条消息有发送者、内容、状态（发送中/已发送）。`isStreaming` 就像微信消息的"发送中"状态。
>
> **⚠️ 注意：前端消息列表 ≠ ChatMemory**
> - 前端消息列表：用于UI显示，断开连接就丢失
> - ChatMemory（后端）：用于AI多轮对话，`conversationId` 隔离
> - 两者是独立的！前端刷新后消息列表清空，但后端ChatMemory仍然保留

---

### 步骤17：AI回复气泡打字机效果 CSS 动画

> **💡 闪烁光标的实现**：
>
> 打字机效果 = 逐字追加文本 + 闪烁光标
> - 逐字追加：由 EventSource.onmessage 回调实现（已在步骤15完成）
> - 闪烁光标：由 CSS 动画实现

在 `AiChat.vue` 的 `<style>` 中添加打字机效果样式：

```css
/* 打字机内容区域（流式输出时应用） */
.typing-content {
  position: relative;
}

/* 闪烁光标动画 */
.cursor-blink {
  display: inline-block;
  color: #409eff;
  font-weight: bold;
  animation: blink 0.8s infinite;  /* 0.8秒闪烁一次 */
}

@keyframes blink {
  0%, 100% { opacity: 1; }   /* 光标可见 */
  50% { opacity: 0; }        /* 光标隐藏 */
}

/* 流式输出结束后，光标自动隐藏 */
/* 当 isStreaming 从 true 变为 false 时，v-if="msg.isStreaming" 为 false */
/* Vue 自动移除 <span class="cursor-blink"> 元素，光标消失 */
```

> 💡 **打字机效果完整流程**：
> 1. AI开始流式输出 → `isStreaming: true` → 显示闪烁光标 `|`
> 2. 每收到一个文本片段 → `content += data` → 文字追加显示
> 3. 流式输出结束 → `isStreaming: false` → 光标消失，显示完整回复

---

### 步骤18：错误处理 + 重连机制

> **💡 前端AI对话的常见错误场景**：
> 1. **网络断开**：EventSource 连接断开
> 2. **API Key 过期**：后端返回 401/403
> 3. **AI 服务不可用**：后端返回 500
> 4. **请求超时**：长时间无响应
> 5. **用户主动取消**：点击停止按钮
>
> **EventSource 的自动重连**：
> - EventSource 内置自动重连——连接断开后，浏览器会自动重新连接
> - 默认重连间隔 3 秒，可以通过 SSE 的 `retry:` 字段自定义
> - 如果服务器设置了 `id:` 字段，重连时会发送 `Last-Event-ID` 请求头
> - **⚠️ 对于AI流式对话，通常不需要自动重连**——因为每次对话都是独立的，断开时AI的上下文已经丢失

在 `AiChat.vue` 的 `<script setup>` 中添加错误处理和重连逻辑：

```javascript
/**
 * 增强版流式发送（带错误处理 + 重连机制）
 *
 * 错误处理策略：
 * 1. 网络错误 → 显示错误提示，不自动重连（因为AI上下文已丢失）
 * 2. 连接超时 → 显示超时提示
 * 3. 主动停止 → 关闭连接，不重连
 */
function sendStreamMessage(message) {
  // 1. 添加空的AI消息占位
  const aiMessage = {
    role: 'assistant',
    content: '',
    isStreaming: true
  }
  messages.value.push(aiMessage)

  // 2. 创建EventSource连接
  const url = `/api/aichat/stream?conversationId=${encodeURIComponent(conversationId.value)}&message=${encodeURIComponent(message)}`
  eventSource = new EventSource(url)

  // 3. 逐字追加
  eventSource.onmessage = function(event) {
    aiMessage.content += event.data
    scrollToBottom()
  }

  // 4. 错误处理
  eventSource.onerror = function(event) {
    // 关闭连接
    if (eventSource) {
      eventSource.close()
      eventSource = null
    }

    // 判断是正常结束还是异常断开
    if (aiMessage.content.length === 0) {
      // 没有收到任何数据就断开了 → 可能是网络错误或服务端错误
      aiMessage.content = '连接失败，请检查网络后重试。'
      aiMessage.isStreaming = false
    } else {
      // 已经收到部分数据后断开 → 标记流式结束
      aiMessage.isStreaming = false
    }

    isLoading.value = false
  }
}

/**
 * 手动停止流式输出
 */
function stopStreaming() {
  if (eventSource) {
    eventSource.close()     // 关闭SSE连接
    eventSource = null
  }
  // 找到最后一条正在流式输出的AI消息，标记结束
  const lastAiMsg = [...messages.value].reverse().find(m => m.role === 'assistant' && m.isStreaming)
  if (lastAiMsg) {
    lastAiMsg.isStreaming = false
  }
  isLoading.value = false
}
```

---

### 步骤19：前后端联调测试

**1. 确保后端服务启动**

```bash
# 运行 SmartApplication.main()
# 确认控制台输出：Started SmartApplication in x.xxx seconds
```

**2. 测试同步模式**

- 打开 AiChat.vue 页面
- 切换到"同步"模式
- 输入"你好"，点击发送
- 观察：等待2-5秒后，AI回复一次性出现

**3. 测试流式模式**

- 切换到"流式"模式
- 输入"介绍一下宿舍管理规定"，点击发送
- 观察：文字逐字逐句出现，有打字机效果和闪烁光标

**4. 测试多轮对话**

- 流式模式下输入"我叫张三住301"
- 等 AI 回复完，再输入"我住哪"
- 确认 AI 回复"你住在301宿舍"（验证 ChatMemory 在流式模式下正常）

**5. 测试 sse-test.html**

```
http://localhost:8080/sse-test.html
```

- 测试 AI 流式对话
- 测试报修进度推送
- 测试同步 vs 流式对比

> 🔧 **常见问题排查**：
> | 问题 | 原因 | 解决方案 |
> |------|------|---------|
> | EventSource 连接后立即断开 | 后端未启动或接口报错 | 检查后端日志，确认接口可访问 |
> | 流式输出中文乱码 | 响应编码问题 | 确认 `produces = TEXT_EVENT_STREAM_VALUE` |
> | 流式数据一次性显示 | 浏览器缓存 | 加时间戳参数 `&t=Date.now()` |
> | ChatMemory 流式模式不记忆 | 未传 conversationId | 确认 URL 包含 `conversationId` 参数 |
> | 404 Not Found | 静态资源路径错误 | 确认 `sse-test.html` 在 `static/` 目录下 |

---

## Part 4：项目代码优化（11:30-12:00）

### 步骤20：创建 GlobalExceptionHandler 全局异常处理

> **💡 为什么需要全局异常处理？**
>
> 在 Day1 的 Service 层中，我们用 try-catch 包裹了每个方法。但这有一个问题：
> - 每个 Service 方法都要写 try-catch → 代码重复
> - 如果某个 Controller 忘记处理异常 → 返回 500 错误页面（不友好）
> - 异常处理逻辑分散在各个方法中 → 难以统一管理
>
> **`@ControllerAdvice` + `@ExceptionHandler` 的优势**：
> - **统一入口**：所有 Controller 抛出的异常都会被自动捕获
> - **代码简洁**：Service 层不需要 try-catch，直接 throw
> - **格式统一**：所有错误都返回 `Result` 格式
> - **类比理解**：全局异常处理就像公司的"投诉处理中心"——各部门不需要自己处理投诉，统一交给投诉中心处理

创建 `com.huashan.smart.common.GlobalExceptionHandler.java`：

```java
package com.huashan.smart.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一捕获Controller层抛出的异常，返回标准Result格式
 *
 * @RestControllerAdvice 的作用：
 * - 这是Spring提供的AOP注解，作用范围是所有@RestController
 * - 当Controller中抛出异常时，Spring会自动找到对应的@ExceptionHandler方法处理
 * - 不需要每个Controller方法都写try-catch
 *
 * @ExceptionHandler 的作用：
 * - 指定该方法处理哪种类型的异常
 * - Spring会自动将异常对象注入方法参数
 * - 返回值会自动序列化为JSON（因为是@RestControllerAdvice）
 *
 * 异常匹配优先级：
 * - 精确匹配优先：IllegalArgumentException > RuntimeException > Exception
 * - 如果没有精确匹配，会向上查找父类
 */
@Slf4j
@RestControllerAdvice   // 标记为全局异常处理器（对所有@RestController生效）
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常
     * 例如：用户未传必填参数、参数格式不对
     *
     * 场景：GET /api/aichat/stream?message=  （message为空）
     * → Spring抛出IllegalArgumentException
     * → 本方法捕获，返回 Result.error(400, "参数不能为空")
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        // warn级别：参数校验失败不是系统bug，只是用户输入问题
        log.warn("参数校验异常：{}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理AI服务相关异常
     * 例如：API Key无效、模型服务不可用、请求超时
     *
     * 场景：DeepSeek API Key过期
     * → Spring AI抛出RuntimeException
     * → 本方法捕获，返回 Result.error(503, "AI服务暂时不可用")
     *
     * ⚠️ 注意：不把原始异常信息暴露给前端
     * - 原始信息可能包含API Key、服务器路径等敏感信息
     * - 返回友好的错误提示即可
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常：", e);
        // 不暴露原始异常信息给前端（安全考虑）
        return Result.error(503, "AI服务暂时不可用，请稍后再试");
    }

    /**
     * 处理所有其他异常（兜底）
     * 任何未被上面两个方法捕获的异常，都会走到这里
     *
     * 这是最后一道防线——确保任何异常都不会返回500错误页面
     * 而是返回统一的Result格式
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.error(500, "系统异常，请联系管理员");
    }
}
```

> 💡 **有了 GlobalExceptionHandler 后，Service 层可以简化**：
> ```java
> // 之前（Day1）：每个方法都要try-catch
> @Override
> public String chat(String conversationId, String message) {
>     try {
>         return chatClient.prompt().user(message).advisors(...).call().content();
>     } catch (Exception e) {
>         log.error("AI对话异常：", e);
>         return "AI服务暂时不可用";
>     }
> }
>
> // 现在（Day2）：直接抛异常，由GlobalExceptionHandler统一处理
> @Override
> public String chat(String conversationId, String message) {
>     return chatClient.prompt().user(message).advisors(...).call().content();
>     // 如果出错，Spring会自动抛出RuntimeException
>     // GlobalExceptionHandler会捕获并返回Result.error(503, "AI服务暂时不可用")
> }
> ```
>
> 但流式方法仍然建议保留 try-catch，因为 `Flux<String>` 的异常处理机制不同——Flux 的错误发生在异步流中，不会被 `@ExceptionHandler` 捕获。

---

### 步骤21：创建 AiChatStreamController——专门处理流式对话

> **💡 为什么需要专门的流式 Controller？**
>
> 之前的 `AiChatController` 把同步和流式放在同一个类中。但在实际项目中，流式对话有更多需求：
> - 自定义 SSE 事件类型（content / done / error）
> - 支持前端断线重连
> - 更完善的错误处理
>
> 所以我们把流式对话独立出来，创建专门的 `AiChatStreamController`。

创建 `com.huashan.smart.controller.AiChatStreamController.java`：

```java
package com.huashan.smart.controller;

import com.huashan.smart.service.AiChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * 流式AI对话控制器（专门处理流式对话）
 * 整合ChatMemory + 流式 + 自定义SSE事件类型
 *
 * 和 AiChatController 的区别：
 * - AiChatController：同步 + 基础流式（Flux<String>，默认message事件类型）
 * - AiChatStreamController：高级流式（Flux<ServerSentEvent<String>>，自定义事件类型）
 *
 * SSE 事件类型定义：
 * - event:content  → AI回复的文本片段（前端追加显示）
 * - event:done     → AI回复完毕（前端关闭连接）
 * - event:error    → 出错（前端显示错误提示）
 *
 * 前端监听方式：
 * eventSource.addEventListener("content", (e) => { 追加文本 })
 * eventSource.addEventListener("done",    (e) => { 关闭连接 })
 * eventSource.addEventListener("error",   (e) => { 显示错误 })
 */
@Slf4j
@RestController
@RequestMapping("/api/aichat")
public class AiChatStreamController {

    private final AiChatService aiChatService;

    public AiChatStreamController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    /**
     * 高级流式对话（带自定义SSE事件类型 + ChatMemory）
     * GET /api/aichat/stream-v2?conversationId=user001&message=你好
     *
     * 和 /api/aichat/stream 的区别：
     * - /stream   → 返回 Flux<String>，默认event=message
     * - /stream-v2 → 返回 Flux<ServerSentEvent<String>>，自定义event=content/done/error
     *
     * 执行流程：
     * 1. 调用 aiChatService.chatStream() 获取 Flux<String>
     * 2. 用 .map() 将每个 String 片段转为 ServerSentEvent
     *    - 正常片段 → event:content, data:文本
     * 3. 用 .concatWith() 在流末尾追加一个 done 事件
     *    - → event:done, data:[DONE]
     * 4. 用 .onErrorResume() 处理异常
     *    - 出错时 → event:error, data:错误信息
     *
     * 前端接收方式：
     * const es = new EventSource("/api/aichat/stream-v2?conversationId=user001&message=你好");
     * es.addEventListener("content", (e) => { aiMessage.content += e.data; });
     * es.addEventListener("done", (e) => { es.close(); });
     * es.addEventListener("error", (e) => { showError(e.data); });
     */
    @GetMapping(value = "/stream-v2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatStreamV2(
            @RequestParam String conversationId,
            @RequestParam String message) {

        return aiChatService.chatStream(conversationId, message)
                // 1. 将每个文本片段包装为 ServerSentEvent（event=content）
                .map(chunk -> ServerSentEvent.<String>builder()
                        .event("content")        // 事件类型：content（AI回复内容）
                        .data(chunk)             // 消息数据：文本片段
                        .build())
                // 2. 在流末尾追加 done 事件
                .concatWith(Flux.just(
                        ServerSentEvent.<String>builder()
                                .event("done")          // 事件类型：done（回复完毕）
                                .data("[DONE]")         // 完成信号
                                .build()))
                // 3. 异常处理：出错时返回 error 事件
                .onErrorResume(e -> {
                    log.error("流式对话异常：", e);
                    return Flux.just(
                            ServerSentEvent.<String>builder()
                                    .event("error")        // 事件类型：error（出错）
                                    .data("AI服务暂时不可用")  // 错误信息
                                    .build());
                });
    }
}
```

> 💡 **stream-v2 的优势**：
> | 对比项 | /stream（基础版） | /stream-v2（高级版） |
> |--------|------------------|---------------------|
> | 返回类型 | `Flux<String>` | `Flux<ServerSentEvent<String>>` |
> | SSE事件类型 | 默认 `message` | 自定义 `content`/`done`/`error` |
> | 完成信号 | 无（连接直接断开） | `event:done, data:[DONE]` |
> | 错误处理 | 无（连接断开） | `event:error, data:错误信息` |
> | 前端监听 | `onmessage`（混在一起） | `addEventListener`（按类型分开） |
> | ChatMemory | 支持 | 支持 |

---

### 步骤22：Day2 总结 + 完整功能验证

**1. 验证流式输出**

```
http://localhost:8080/chat/stream?message=你好
```
→ 文字逐字出现

**2. 验证 ChatMemory + 流式**

```
http://localhost:8080/api/aichat/stream?conversationId=test001&message=我叫张三住301
→ 等回复完
http://localhost:8080/api/aichat/stream?conversationId=test001&message=我住哪
→ "你住在301宿舍"
```

**3. 验证 SSE 自定义事件类型**

```
http://localhost:8080/api/sse/repair-progress?repairId=R001
→ 每隔2秒推送一个阶段
```

**4. 验证高级流式接口**

```
http://localhost:8080/api/aichat/stream-v2?conversationId=test002&message=你好
→ SSE事件类型为content，最后有done信号
```

**5. 验证全局异常处理**

```
http://localhost:8080/api/aichat/stream?conversationId=test&message=
→ 返回错误信息而不是500页面
```

**6. 验证前端测试页面**

```
http://localhost:8080/sse-test.html
→ 三个模块全部可用
```

---

## Day2 完整项目文件清单

```
src/
├── main/
│   ├── java/com/huashan/smart/
│   │   ├── SmartApplication.java
│   │   ├── common/
│   │   │   ├── Result.java                       ← Day1 已有
│   │   │   └── GlobalExceptionHandler.java       ← Day2 新增
│   │   ├── config/
│   │   │   └── AiConfig.java                     ← Day1 已有
│   │   ├── controller/
│   │   │   ├── SimpleChatController.java         ← Day1 已有
│   │   │   ├── ModeController.java               ← Day1 已有
│   │   │   ├── MemoryChatController.java         ← Day1 已有
│   │   │   ├── StructuredController.java         ← Day1 已有
│   │   │   ├── RoleChatController.java           ← Day1 已有
│   │   │   ├── RtfPromptController.java          ← Day1 已有
│   │   │   ├── AiChatController.java             ← Day1 已有，Day2 新增流式端点
│   │   │   ├── StreamChatController.java         ← Day2 新增：Flux流式演示
│   │   │   ├── SseEmitterChatController.java      ← Day2 新增：SseEmitter对比
│   │   │   ├── SSEChatController.java             ← Day2 新增：SSE自定义事件
│   │   │   └── AiChatStreamController.java        ← Day2 新增：高级流式+ChatMemory
│   │   ├── model/
│   │   │   └── StudentInfo.java                  ← Day1 已有
│   │   └── service/
│   │       ├── AiChatService.java                ← Day1 已有，Day2 新增 chatStream
│   │       └── impl/
│   │           └── AiChatServiceImpl.java        ← Day1 已有，Day2 新增 chatStream 实现
│   └── resources/
│       ├── application.yaml
│       └── static/
│           └── sse-test.html                      ← Day2 新增：SSE测试页面
└── pom.xml
```

---

## Day2 知识点总结

| 序号 | 知识点 | 核心代码 | 关键要点 |
|------|--------|---------|---------|
| 1 | 同步→流式 | `.call()` → `.stream()` | 只改一个方法，调用链完全一致 |
| 2 | Flux流式输出 | `Flux<String>` + `TEXT_EVENT_STREAM_VALUE` | Spring自动将Flux转为SSE格式 |
| 3 | SseEmitter对比 | `SseEmitter` + 线程池 | 30+行 vs Flux 1行，推荐Flux |
| 4 | SSE消息格式 | `data:` / `event:` / `id:` / 空行分隔 | HTML5标准，浏览器内置支持 |
| 5 | 自定义事件类型 | `ServerSentEvent.builder().event("xxx")` | 前端用addEventListener按类型监听 |
| 6 | 延时推送 | `Flux.delayElements(Duration.ofSeconds(2))` | 模拟实时进度更新 |
| 7 | 前端EventSource | `new EventSource(url)` + `onmessage` | 浏览器内置SSE客户端API |
| 8 | 打字机效果 | `aiMessage.content += event.data` | 逐字追加 + CSS闪烁光标 |
| 9 | ChatMemory+流式 | `.advisors(param).stream().content()` | ChatMemory在流式模式下同样有效 |
| 10 | 全局异常处理 | `@RestControllerAdvice` + `@ExceptionHandler` | 统一异常捕获和响应格式 |
| 11 | 高级流式Controller | `Flux<ServerSentEvent<String>>` | content/done/error三种事件类型 |
| 12 | 断线重连 | EventSource自动重连 + `Last-Event-ID` | 浏览器内置，无需手动实现 |

---

## Day1 + Day2 API 接口总览

| 接口 | 方法 | 路径 | 返回类型 | 说明 |
|------|------|------|---------|------|
| 最简对话 | GET | `/chat?message=xxx` | String | Day1 |
| Token统计 | GET | `/chat/info?message=xxx` | Map | Day1 |
| System Prompt | GET | `/mode/system?question=xxx` | String | Day1 |
| 参数化模板 | GET | `/mode/template?building=x&student=x` | String | Day1 |
| 多轮对话 | GET | `/memory/chat?conversationId=x&message=x` | String | Day1 |
| 结构化输出 | GET | `/struct/student?desc=xxx` | StudentInfo | Day1 |
| 角色对话 | GET | `/role/dormitory?message=xxx` | String | Day1 |
| RTF框架 | GET | `/rtf/chat?message=xxx` | String | Day1 |
| 同步对话 | POST | `/api/aichat` | Result\<String\> | Day1 |
| **流式对比** | GET | `/chat/sync?message=xxx` | String | Day2 |
| **Flux流式** | GET | `/chat/stream?message=xxx` | Flux\<String\> | Day2 |
| **流式对话** | GET | `/api/aichat/stream?conversationId=x&message=x` | Flux\<String\> | Day2 |
| **SseEmitter** | GET | `/sse-emitter/chat?message=xxx` | SseEmitter | Day2 |
| **报修进度** | GET | `/api/sse/repair-progress?repairId=x` | Flux\<SSE\> | Day2 |
| **高级流式** | GET | `/api/aichat/stream-v2?conversationId=x&message=x` | Flux\<SSE\> | Day2 |
