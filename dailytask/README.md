# 实训项目：Spring AI 智能宿舍管理系统

## 项目信息

| 项目 | 说明 |
|------|------|
| 技术栈 | Spring Boot 3.5.15 + Spring AI 1.0.0 + JDK 17+ |
| AI模型 | DeepSeek（deepseek-chat） |
| 包名 | com.lbya.smart |

---

## 运行步骤

### 1. 用 IDEA 打开项目
- File → Open → 选择 `dailytask` 文件夹
- 等待右下角 Maven 索引进度条走完

### 2. 配置 API Key
打开 `src/main/resources/application.yaml`，替换 api-key：
```yaml
api-key: sk-你的DeepSeekAPIKey
```
> 从 https://platform.deepseek.com 获取 API Key

### 3. 启动项目
打开 `SmartApplication.java` → 点击 main 方法左侧绿色三角 ▶ → Run

控制台出现 `Started SmartApplication` 即启动成功。

---

## 测试方式

启动成功后，直接打开浏览器，在地址栏粘贴以下地址访问即可。

### Day1：同步调用

#### 模式一：最简调用
```
http://localhost:8080/chat?message=你好
http://localhost:8080/chat/info?message=你好
http://localhost:8080/chat/safe?message=你好
http://localhost:8080/chat/strict?message=写一首关于春天的诗
```

#### 模式二：System Prompt
```
http://localhost:8080/mode/system?question=宿舍几点熄灯
```

#### 模式三：多轮对话
```
http://localhost:8080/memory/chat?conversationId=user001&message=我叫张三住301
http://localhost:8080/memory/chat?conversationId=user001&message=我住哪
```

#### 模式四：参数化模板
```
http://localhost:8080/mode/template?building=3&student=张三
```

#### 模式五：结构化输出
```
http://localhost:8080/struct/student?desc=李四住5号楼203，手机13912345678，本月缺勤2次
```

#### 多角色对比
```
http://localhost:8080/role/compare?message=帮我查成绩
```

#### RTF Prompt
```
http://localhost:8080/rtf/chat?message=我的宿舍水龙头坏了
```

#### 同步正式接口
```bash
curl -X POST http://localhost:8080/api/aichat \
  -H "Content-Type: application/json" \
  -d '{"conversationId":"user001","message":"我叫张三住301"}'
```

---

### Day2：流式输出 + SSE

#### Flux 流式对话
```
http://localhost:8080/chat/stream?message=你好
```
浏览器逐字显示 AI 回复，像 ChatGPT 一样。

#### 同步 vs 流式对比
```
http://localhost:8080/chat/sync?message=请介绍高校宿舍管理规定
http://localhost:8080/chat/stream?message=请介绍高校宿舍管理规定
```

#### ChatMemory + 流式
```
http://localhost:8080/api/aichat/stream?conversationId=test001&message=我叫张三住301
http://localhost:8080/api/aichat/stream?conversationId=test001&message=我住哪
```

#### 报修进度推送（SSE自定义事件类型）
```
http://localhost:8080/api/sse/repair-progress?repairId=R001
```
每隔2秒推送一个阶段：已提交 → 已派单 → 修理中 → 已完成。

#### 高级流式（自定义事件类型）
```
http://localhost:8080/api/aichat/stream-v2?conversationId=test002&message=你好
```
SSE 事件类型为 content，最后有 done 信号。

#### SseEmitter 对比（了解即可）
```
http://localhost:8080/sse-emitter/chat?message=你好
```

#### SSE 测试页面
```
http://localhost:8080/sse-test.html
```
三个模块：AI流式对话、报修进度推送、同步vs流式对比。

---

## 项目结构

```
dailytask/
├── pom.xml
├── src/main/resources/
│   ├── application.yaml
│   └── static/
│       └── sse-test.html                          ← Day2：SSE测试页面
└── src/main/java/com/lbya/smart/
    ├── SmartApplication.java
    ├── common/
    │   ├── Result.java
    │   └── GlobalExceptionHandler.java            ← Day2：全局异常处理
    ├── config/
    │   └── AiConfig.java
    ├── controller/
    │   ├── SimpleChatController.java              ← 模式一：最简调用
    │   ├── ModeController.java                    ← 模式二+四
    │   ├── MemoryChatController.java              ← 模式三：多轮对话
    │   ├── StructuredController.java              ← 模式五：结构化输出
    │   ├── RoleChatController.java                ← 多角色对话
    │   ├── RtfPromptController.java               ← RTF框架Prompt
    │   ├── AiChatController.java                  ← 同步+基础流式
    │   ├── StreamChatController.java              ← Day2：Flux流式演示
    │   ├── SseEmitterChatController.java          ← Day2：SseEmitter对比
    │   ├── SSEChatController.java                 ← Day2：SSE自定义事件
    │   └── AiChatStreamController.java            ← Day2：高级流式+ChatMemory
    ├── model/
    │   └── StudentInfo.java
    └── service/
        ├── AiChatService.java
        └── impl/
            └── AiChatServiceImpl.java
```

---

## 知识点速查

### Day1：同步调用五种模式
| 模式 | 核心代码 | 用途 |
|------|---------|------|
| 模式一 | `.prompt().user(msg).call().content()` | 最简调用 |
| 模式二 | `.system("角色").user(msg).call().content()` | 给AI设定角色 |
| 模式三 | `.advisors(memory).user(msg).call().content()` | 多轮对话 |
| 模式四 | `.user(u -> u.text("{key}").param("key", val))` | 参数化模板 |
| 模式五 | `.call().entity(Xxx.class)` | 结构化输出 |

### Day2：流式输出
| 知识点 | 核心代码 | 说明 |
|--------|---------|------|
| 同步→流式 | `.call()` → `.stream()` | 只改一个方法 |
| Flux流式 | `Flux<String>` + `TEXT_EVENT_STREAM_VALUE` | Spring自动转SSE |
| SSE事件类型 | `ServerSentEvent.builder().event("xxx")` | 前端按类型监听 |
| 打字机效果 | `aiMessage.content += event.data` | 逐字追加+CSS光标 |
| 全局异常处理 | `@RestControllerAdvice` + `@ExceptionHandler` | 统一异常捕获 |
