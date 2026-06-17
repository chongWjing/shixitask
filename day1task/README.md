# Day1：Spring AI 项目搭建与同步调用

## 项目信息

| 项目 | 说明 |
|------|------|
| 技术栈 | Spring Boot 3.5.15 + Spring AI 1.0.0 + JDK 17+ |
| AI模型 | DeepSeek（deepseek-chat） |
| 包名 | com.huashan.smart |

---

## 运行步骤

### 1. 用 IDEA 打开项目
- File → Open → 选择 `day1task` 文件夹
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

### 模式一：最简调用

```
http://localhost:8080/chat?message=你好
```

浏览器直接显示 AI 回复的文本。

```
http://localhost:8080/chat/info?message=你好
```

浏览器显示 JSON，包含 `content`（回复内容）和 Token 消耗统计。

```
http://localhost:8080/chat/safe?message=你好
```

成功返回 `{"success":true, "reply":"..."}`。

```
http://localhost:8080/chat/strict?message=写一首关于春天的诗
```

temperature=0.1，多次刷新输出几乎一致。

### 模式二：System Prompt

```
http://localhost:8080/mode/system?question=宿舍几点熄灯
```

AI 以"宿舍助手"角色回答，简洁不超过50字。

### 模式三：多轮对话

先发第一条：
```
http://localhost:8080/memory/chat?conversationId=user001&message=我叫张三住301
```

再发第二条（AI能记住上一条）：
```
http://localhost:8080/memory/chat?conversationId=user001&message=我住哪
```

AI 会回答"你住在301宿舍"。

换个 conversationId（AI不记得之前的对话）：
```
http://localhost:8080/memory/chat?conversationId=user002&message=我住哪
```

AI 会回答"还不了解你的住宿信息"。

### 模式四：参数化模板

```
http://localhost:8080/mode/template?building=3&student=张三
```

模板中的 `{building}` 和 `{student}` 被替换为实际值。

### 模式五：结构化输出

```
http://localhost:8080/struct/student?desc=李四住5号楼203，手机13912345678，本月缺勤2次
```

返回 JSON 格式的学生信息：
```json
{
  "name": "李四",
  "building": "5",
  "room": "203",
  "phone": "13912345678",
  "attendanceStatus": "缺勤2次"
}
```

### 多角色对比

```
http://localhost:8080/role/compare?message=帮我查成绩
```

返回两个角色的不同回答，对比效果明显。

### RTF Prompt

```
http://localhost:8080/rtf/chat?message=我的宿舍水龙头坏了
```

触发报修引导流程。

### 流式对话（打字机效果）

```
http://localhost:8080/api/aichat/stream?conversationId=user001&message=讲个笑话
```

浏览器逐字显示 AI 回复，像 ChatGPT 一样。

### 同步正式接口

用 Postman 或 curl 测试：

```bash
curl -X POST http://localhost:8080/api/aichat \
  -H "Content-Type: application/json" \
  -d '{"conversationId":"user001","message":"我叫张三住301"}'
```

返回统一格式：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "你好张三同学！已记录你住在301宿舍，有什么可以帮你的吗？"
}
```

---

## 接口说明

### 模式一：最简调用
| 接口 | 方法 | 说明 |
|------|------|------|
| `/chat?message=你好` | GET | 最简AI对话 |
| `/chat/info?message=你好` | GET | 返回AI回复 + Token消耗统计 |
| `/chat/safe?message=你好` | GET | 带错误处理，异常时返回友好提示 |
| `/chat/strict?message=写一首诗` | GET | temperature=0.1，输出稳定 |

### 模式二：System Prompt
| 接口 | 方法 | 说明 |
|------|------|------|
| `/mode/system?question=宿舍几点熄灯` | GET | 临时指定AI角色为宿舍助手 |

### 模式三：ChatMemory 多轮对话
| 接口 | 方法 | 说明 |
|------|------|------|
| `/memory/chat?conversationId=user001&message=我叫张三住301` | GET | 第一轮对话 |
| `/memory/chat?conversationId=user001&message=我住哪` | GET | 第二轮，AI能记住上下文 |
| `/memory/chat?conversationId=user002&message=我住哪` | GET | 不同conversationId，互不干扰 |

### 模式四：参数化模板
| 接口 | 方法 | 说明 |
|------|------|------|
| `/mode/template?building=3&student=张三` | GET | 模板中的 {building}{student} 被实际值替换 |

### 模式五：结构化输出
| 接口 | 方法 | 说明 |
|------|------|------|
| `/struct/student?desc=李四住5号楼203，手机13912345678，本月缺勤2次` | GET | AI按StudentInfo格式返回JSON |

### 多角色对话
| 接口 | 方法 | 说明 |
|------|------|------|
| `/role/dormitory?message=帮我查成绩` | GET | 宿舍助手"小宿"回答 |
| `/role/tutor?message=什么是Spring Boot` | GET | 编程老师"王老师"回答 |
| `/role/compare?message=帮我查成绩` | GET | 同一问题，两个角色对比 |

### RTF Prompt
| 接口 | 方法 | 说明 |
|------|------|------|
| `/rtf/chat?message=我的宿舍水龙头坏了` | GET | 使用RTF框架Prompt，触发报修引导 |

### 正式接口（分层架构）
| 接口 | 方法 | 说明 |
|------|------|------|
| `POST /api/aichat` | POST | 同步对话，请求体：`{"conversationId":"user001","message":"你好"}` |
| `GET /api/aichat/stream?conversationId=user001&message=你好` | GET | 流式SSE对话，打字机效果 |

---

## 项目结构

```
day1task/
├── pom.xml                                    ← Maven依赖
├── src/main/resources/
│   └── application.yaml                       ← DeepSeek API配置
└── src/main/java/com/huashan/smart/
    ├── SmartApplication.java                  ← 启动类
    ├── common/
    │   └── Result.java                        ← 统一响应格式
    ├── config/
    │   └── AiConfig.java                      ← ChatMemory + 多角色ChatClient + RTF Prompt
    ├── controller/
    │   ├── SimpleChatController.java          ← 模式一：最简调用
    │   ├── ModeController.java                ← 模式二+四：System Prompt + 参数化模板
    │   ├── MemoryChatController.java          ← 模式三：ChatMemory多轮对话
    │   ├── StructuredController.java          ← 模式五：结构化输出
    │   ├── RoleChatController.java            ← 多角色对话
    │   ├── RtfPromptController.java           ← RTF框架Prompt
    │   └── AiChatController.java              ← 正式接口（同步+流式）
    ├── model/
    │   └── StudentInfo.java                   ← 结构化输出Entity
    └── service/
        ├── AiChatService.java                 ← Service接口
        └── impl/
            └── AiChatServiceImpl.java         ← Service实现
```

---

## 五种同步调用模式速查

| 模式 | 核心代码 | 用途 |
|------|---------|------|
| 模式一 | `.prompt().user(msg).call().content()` | 最简调用 |
| 模式二 | `.system("角色").user(msg).call().content()` | 给AI设定角色 |
| 模式三 | `.advisors(memory).user(msg).call().content()` | 多轮对话，AI记住上下文 |
| 模式四 | `.user(u -> u.text("{key}").param("key", val))` | 参数化模板，防止Prompt注入 |
| 模式五 | `.call().entity(Xxx.class)` | AI按Java类格式返回结构化数据 |
