# Day3 - MCP 全实操与多模型

## 任务目标

实现 MCP（Model Context Protocol）工具调用 + 多模型智能路由。

## 完成内容

### dailytask（MCP Client + 多模型）

| 文件 | 功能 |
|------|------|
| `McpTestController.java` | MCP 工具调用测试 |
| `MultiModelConfig.java` | 多模型配置（V3 + R1） |
| `MultiModelController.java` | 智能路由控制器 |

### mcp-calculator-server（MCP Server）

| 文件 | 功能 |
|------|------|
| `CalculatorTool.java` | 7 个计算器工具（@Tool 注解） |
| `EmailTool.java` | 邮件通知工具（HTML 格式） |

## 技术点

- MCP 协议：Client-Server 架构
- `@Tool` + `@ToolParam` 注解定义工具
- `.toolNames()` 调用指定工具（Spring AI 1.0）
- `@Primary` + `@Qualifier` 多模型注入
- 关键词匹配智能路由

## 智能路由规则

| 关键词 | 路由到 |
|--------|--------|
| 推导、证明、分析、为什么、原理、逻辑 | R1（推理模型） |
| 其他 | V3（快速模型） |

## 运行方式

```bash
# 1. 先构建 MCP Server
cd mcp-calculator-server
mvn package

# 2. 启动主项目（会自动连接 MCP Server）
cd dailytask
mvn spring-boot:run

# 3. 测试 MCP 工具
curl "http://localhost:8080/mcp/add?a=10&b=20" -H "Authorization: Bearer <token>"

# 4. 测试智能路由
curl "http://localhost:8080/model/smart?message=为什么天是蓝色的" -H "Authorization: Bearer <token>"
```
