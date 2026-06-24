# 实训项目：Spring AI 智能宿舍管理系统

## 项目信息

| 项目 | 说明 |
|------|------|
| 技术栈 | Spring Boot 3.5.15 + Spring AI 1.0.0 + JDK 17+ |
| AI模型 | DeepSeek（deepseek-chat V3 + deepseek-reasoner R1） |
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

### Day3：MCP 工具调用 + 多模型

> **前置步骤**：先打包 MCP Server
> ```bash
> cd C:/Users/W2774/Desktop/实习作业/mcp-calculator-server
> mvn clean package -DskipTests
> ```
> 打包成功后重启 dailytask 主项目，Spring AI 会自动启动 MCP Server 进程。

#### MCP 工具调用
```
http://localhost:8080/mcp/chat?message=123加456等于多少
http://localhost:8080/mcp/chat?message=1000减去378等于多少
http://localhost:8080/mcp/chat?message=25乘以4等于多少
http://localhost:8080/mcp/chat?message=10除以3等于多少
http://localhost:8080/mcp/chat?message=85、90、78、92这四个数的平均分是多少
```

#### 宿舍容量计算（AI 自动调用业务工具）
```
http://localhost:8080/mcp/chat?message=3号楼有150间房每间住4人目前住了480人请问还有多少空床位
http://localhost:8080/mcp/chat?message=3号楼总共600个床位住了480人还有多少空床位
```

#### AI 决策演示（不需要改代码，问不同问题自动选不同工具）
```
http://localhost:8080/mcp/chat?message=88加12等于多少         → 自动调 add
http://localhost:8080/mcp/chat?message=5号楼200间房每间6人住了960人 → 自动调 calculateDormitoryCapacity
http://localhost:8080/mcp/chat?message=今天天气怎么样            → 不调工具，自己回答
```

#### 多模型对话
```
http://localhost:8080/model/v3?message=你好，请介绍一下你自己        → V3 快速回复
http://localhost:8080/model/r1?message=请推导勾股定理            → R1 深度推理
```

#### 智能路由（关键词自动选模型）
```
http://localhost:8080/model/smart?message=宿舍几点熄灯              → 匹配失败 → V3
http://localhost:8080/model/smart?message=请分析为什么3号楼入住率低   → 匹配"分析" → R1
http://localhost:8080/model/smart?message=请推导一元二次方程求根公式  → 匹配"推导" → R1
```

---

### Day4：用户管理（对接前端商城）

#### 用户注册 + 登录
```bash
# 注册新用户
curl -X POST http://localhost:8080/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"zhangsan","password":"123456","phone":"13912345678"}'

# 登录
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'
```

登录成功后返回 `{ code:200, data: { token:"Bearer xxx", user:{ id, username, role, phone } } }`

#### 需登录的接口（带 token）
```bash
# 获取用户信息
curl -H "Authorization: Bearer <token>" http://localhost:8080/user/info

# 退出登录
curl -X POST -H "Authorization: Bearer <token>" http://localhost:8080/user/logout
```

> 前端项目见 `frontend/` 目录，启动方式见 `frontend/README.md`

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
    │   ├── AiConfig.java
    │   ├── MultiModelConfig.java                  ← Day3：V3+R1双模型配置
    │   └── WebConfig.java                         ← Day4：CORS跨域+认证拦截器
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
    │   ├── AiChatStreamController.java            ← Day2：高级流式+ChatMemory
    │   ├── McpTestController.java                 ← Day3：MCP工具调用测试
    │   ├── MultiModelController.java              ← Day3：多模型+智能路由
    │   └── UserController.java                    ← Day4：用户注册/登录/退出/信息
    ├── interceptor/
    │   ├── AuthInterceptor.java                   ← Day4：Token认证拦截器
    │   └── TokenStore.java                        ← Day4：Token内存存储
    ├── model/
    │   ├── StudentInfo.java
    │   └── User.java                              ← Day4：用户实体
    └── service/
        ├── AiChatService.java
        ├── UserService.java                       ← Day4：用户服务接口
        └── impl/
            ├── AiChatServiceImpl.java
            └── UserServiceImpl.java               ← Day4：用户服务实现（内存存储）

mcp-calculator-server/                              ← Day3：独立MCP Server项目
├── pom.xml
└── src/main/
    ├── java/com/lbya/mcp/
    │   ├── McpCalculatorServerApplication.java
    │   └── tool/
    │       ├── CalculatorTool.java                 ← 7个@Tool：加减乘除+平均+宿舍容量+空床位
    │       └── EmailTool.java                      ← 1个@Tool：入住通知邮件
    └── resources/
        └── application.yml                         ← MCP Server名+SMTP配置
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

### Day3：MCP + 多模型
| 知识点 | 核心代码 | 说明 |
|--------|---------|------|
| MCP Server | `@Tool` + `@ToolParam` | description 是 AI 决策依据 |
| MCP Client | `.toolNames("add", ...)` | 注册后 AI 自动选择调用 |
| AI 自动决策 | 无 if-else | AI 根据 description 自己选工具 |
| description优化 | 做什么+何时用+区别 | 越精准，AI选择越准确 |
| 多模型 | `@Primary` + `@Qualifier` | V3 快速 + R1 推理 |
| 智能路由 | 关键词匹配选模型 | 简单→V3，推理→R1 |
| Spring AI统一抽象 | 换模型只改配置 | 代码零修改，不绑定厂商 |

### Day4：用户管理 + 前端联调
| 知识点 | 核心代码 | 说明 |
|--------|---------|------|
| 用户实体 | `@Data User` | id/username/password/phone/role |
| 密码加密 | MD5 | 前后端一致的加密方式 |
| Token认证 | `AuthInterceptor` | 从 Authorization 头提取 Bearer token |
| Token存储 | `TokenStore` (ConcurrentHashMap) | MVP内存存储，重启丢失 |
| CORS跨域 | `WebConfig.addCorsMappings` | 允许前端5173访问后端8080 |
| Vite proxy | `vite.config.js proxy` | `/api` → `localhost:8080` 自动代理 |
| axios封装 | `request.js` | baseURL=/api + 响应拦截器 |
| 路由守卫 | `router.beforeEach` | meta.requiresAuth 检查token |
