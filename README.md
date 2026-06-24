# 实习作业 - Spring AI 全栈开发

> 每日任务完成记录，涵盖 MCP、多模型、全栈开发、数据库设计等。

## 项目总结

本项目是一个 **AI 驱动的鲜花电商平台**，采用前后端分离架构，整合了 Spring AI、MCP 协议、多模型智能路由等 AI 能力，同时实现了完整的用户管理和商品展示功能。

### 技术架构

```
┌─────────────────────────────────────────────────────────┐
│                    Vue3 前端 (frontend)                  │
│              Element Plus + Vite + Axios                 │
│         http://localhost:5173  →  proxy → 8080           │
└───────────────────────┬─────────────────────────────────┘
                        │ REST API
┌───────────────────────▼─────────────────────────────────┐
│              Spring Boot 后端 (dailytask)                │
│    ┌──────────┐  ┌──────────┐  ┌──────────────────┐    │
│    │ 用户管理  │  │ AI 对话   │  │ MCP 工具调用      │    │
│    │ JWT+MD5  │  │ V4 Flash │  │ 计算器+邮件       │    │
│    └──────────┘  └──────────┘  └──────────────────┘    │
│                         │                               │
│              ┌──────────▼──────────┐                    │
│              │   MultiModelConfig  │                    │
│              │  智能路由(R1/V3)     │                    │
│              └─────────────────────┘                    │
└───────────────────────┬─────────────────────────────────┘
                        │ stdio / HTTPS
         ┌──────────────▼──────────────┐
         │   DeepSeek API (V4 Flash)   │
         │   MCP Server (Calculator)   │
         └─────────────────────────────┘
```

### 核心功能

| 模块 | 功能 | 技术 |
|------|------|------|
| AI 对话 | 智能客服，支持多轮对话 | Spring AI 1.0 + DeepSeek V4 Flash |
| 智能路由 | 关键词匹配自动选模型 | R1(推理) / V3(通用) |
| MCP 工具 | 计算器、邮件通知 | MCP 协议 + @Tool 注解 |
| 用户系统 | 登录/注册/登出 | JWT + MD5 + 拦截器 |
| 商品展示 | 分页列表、分类筛选 | MyBatis-Plus + MySQL |

## 目录结构

```
实习作业/
├── dailytask/              # Spring AI 主项目（AI对话 + MCP + 多模型 + 用户管理）
├── frontend/               # Vue3 前端（花之恋鲜花电商）
├── day1/                   # Spring AI 项目搭建与同步调用
├── day2/                   # 流式输出与前端AI实现（教程）
├── day3/                   # MCP全实操与多模型（教程）
├── day4/                   # 前端开发（Figma设计稿 + 开发文档）
├── day5/                   # Spring Boot 后端（MyBatis-Plus + JWT + MySQL）
├── day6/                   # 技术手册：SpringBoot项目搭建教程
├── mcp-calculator-server/  # MCP Server（计算器 + 邮件工具）
├── 原型图/                  # 页面原型设计（HTML）
├── figma-plugin/           # Figma 插件（设计稿生成）
└── README.md               # 本文档
```

## 快速启动

### 1. 启动后端（dailytask）

```bash
cd dailytask
mvn spring-boot:run
# 启动在 http://localhost:8080
```

> API Key 已配置在 `application-local.yaml`，使用 DeepSeek V4 Flash

### 2. 启动前端（frontend）

```bash
cd frontend
npm install    # 首次需要
npm run dev
# 启动在 http://localhost:5173
```

### 3. 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| test | 123456 | 普通用户 |

## 接口测试

### 用户管理

```bash
# 登录
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'

# 返回: {"code":200,"data":{"user":{...},"token":"Bearer xxx"}}

# 获取用户信息（需要Token）
curl http://localhost:8080/user/info \
  -H "Authorization: Bearer <token>"

# 注册新用户
curl -X POST http://localhost:8080/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser","password":"123456","phone":"13800138000"}'

# 登出
curl -X POST http://localhost:8080/user/logout \
  -H "Authorization: Bearer <token>"
```

### AI 对话

```bash
# V3 快速对话
curl "http://localhost:8080/model/v3?message=你好" \
  -H "Authorization: Bearer <token>"

# R1 推理模型
curl "http://localhost:8080/model/r1?message=请推导勾股定理" \
  -H "Authorization: Bearer <token>"

# 智能路由（自动选模型）
curl "http://localhost:8080/model/smart?message=为什么天是蓝色的" \
  -H "Authorization: Bearer <token>"
# 含"为什么"等关键词 → 自动路由到R1推理模型
```

### MCP 工具（需先构建 MCP Server）

```bash
# 构建 MCP Server
cd mcp-calculator-server
mvn package

# 测试MCP工具（通过dailytask调用）
curl "http://localhost:8080/mcp/add?a=10&b=20" \
  -H "Authorization: Bearer <token>"
```

## 各日任务详情

### Day1 - Spring AI 项目搭建

搭建 Spring AI 基础项目，实现同步调用、角色设定、结构化输出等基础功能。

**文件**: `day1/` - 7 个 Java 控制器 + 配置文件

### Day2 - 流式输出与前端AI实现

实现 SSE 流式输出，前端实时显示 AI 回复内容。

**文件**: `day2/Day2_流式输出与前端AI实现.md`（教程文档）

### Day3 - MCP 与多模型

- **MCP Client**: 连接本地 MCP Server，调用工具函数
- **多模型**: DeepSeek V3（通用）+ R1（推理）
- **智能路由**: 关键词匹配自动选择模型

**文件**: `dailytask/` + `mcp-calculator-server/`

### Day4 - 全栈开发

- **前端**: Vue3 + Element Plus，登录/首页/商品/购物车等页面
- **后端**: 用户管理（JWT + MD5 + 拦截器）
- **设计**: Figma 设计稿 + HTML 原型

**文件**: `frontend/` + `dailytask/` + `原型图/` + `figma-plugin/`

### Day5 - Spring Boot 后端

完整电商后端：用户、商品、订单、购物车、分类、评价 6 张表。

**文件**: `day5/` - 35 个 Java 文件 + MyBatis-Plus + JWT

### Day6 - 技术手册

Spring Boot 项目搭建教学文档，涵盖统一响应、跨域配置、实体类设计等。

**文件**: `day6/技术手册_第6章_SpringBoot项目搭建.md`

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Vite + Element Plus + Axios |
| 后端 | Spring Boot 3.5.15 + Spring AI 1.0 |
| ORM | MyBatis-Plus 3.5.9 |
| 数据库 | MySQL 8 |
| 认证 | JWT (java-jwt 4.4.0) + MD5 |
| AI | DeepSeek V4 Flash / R1（OpenAI 兼容） |
| 协议 | MCP (Model Context Protocol) |
| 设计 | Figma + HTML 原型 |
