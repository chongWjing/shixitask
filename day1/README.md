# Day1 - Spring AI 项目搭建与同步调用

## 任务目标

搭建 Spring AI 基础项目，实现 AI 模型的同步调用功能。

## 完成内容

| 文件 | 功能 |
|------|------|
| `SmartApplication.java` | Spring Boot 启动类 |
| `AIConfig.java` | Spring AI 配置（DeepSeek 模型连接） |
| `SimpleChatController.java` | 基础对话接口 |
| `RoleChatController.java` | 角色设定对话（System Prompt） |
| `ModelController.java` | 多模型切换调用 |
| `StructuredController.java` | 结构化输出（JSON 格式返回） |
| `StudentInfo.java` | 学生信息实体类 |

## 技术点

- Spring AI 1.0 集成 DeepSeek API
- `ChatClient` 同步调用
- `@SystemMessage` 角色设定
- 结构化输出（Bean 输出）

## 运行方式

```bash
# 配置 API Key
export DEEPSEEK_API_KEY=your-key

# 启动项目
mvn spring-boot:run

# 测试对话
curl "http://localhost:8080/chat?message=你好"
```
