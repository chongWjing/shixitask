# 花之恋鲜花电商平台

> 基于 Spring Boot 3 + Spring AI + Vue 3 的鲜花电商全栈项目

## 项目简介

花之恋是一个鲜花电商平台，包含用户端商城和管理后台。集成 Spring AI + DeepSeek 大模型，支持智能搜索和自然语言查询数据库。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.5.15 + MyBatis-Plus 3.5.9 + MySQL 8.0 |
| 前端 | Vue 3 + Element Plus + Vite |
| AI | Spring AI 1.0.0 + DeepSeek |
| 认证 | JWT + MD5 |

## 项目结构

```
dailytask/
├── backend/                    # 后端 Spring Boot 项目
│   ├── src/main/java/com/lbya/backend/
│   │   ├── config/            # 配置类（CORS、JWT、AI、多模型）
│   │   ├── controller/        # 控制器（23个）
│   │   ├── entity/            # 实体类
│   │   ├── mapper/            # MyBatis Mapper
│   │   ├── service/           # 业务逻辑层
│   │   └── util/              # 工具类（JWT、MD5、Result）
│   └── src/main/resources/
│       ├── application.yaml           # 主配置
│       ├── application-local.yaml     # 本地配置（已忽略）
│       └── sql/init.sql              # 数据库初始化脚本
├── frontend/                   # 前端 Vue 3 项目
│   └── src/
│       ├── api/               # API 接口封装
│       ├── router/            # 路由配置
│       └── views/
│           ├── shop/          # 商城页面（首页、登录、商品详情、购物车、订单）
│           └── admin/         # 后台页面（登录、仪表盘、商品管理、订单管理、AI查询）
└── README.md
```

## 功能特性

### 用户端商城
- 首页轮播图 + 分类导航
- 商品列表（分页、搜索）
- 商品详情 + 评价
- 购物车管理
- 下单 / 支付 / 确认收货
- 用户注册 / 登录

### 管理后台
- 数据仪表盘（销售额、订单数、商品数、趋势图表）
- 商品管理（增删改查、上下架）
- 订单管理（列表、发货、状态变更）
- AI 智能查询（自然语言 → SQL）

### Spring AI 功能
- 同步对话 / 流式输出（SSE）
- 多模型对话（V3快速 + R1推理）
- 角色扮演对话
- 上下文记忆（20条消息窗口）
- 结构化输出（AI → Java对象）
- MCP 工具调用
- RTF Prompt 框架
- AI 智能表格查询（NL → SQL，5层安全校验）

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0+

### 1. 初始化数据库

```bash
# 登录 MySQL 执行初始化脚本
mysql -u root -p123456 < dailytask/backend/src/main/resources/sql/init.sql
```

或在 MySQL 客户端中打开 `init.sql` 执行。

**默认账号：**
| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 管理员 |
| test | 123456 | 普通用户 |
| lily | 123456 | 普通用户 |

### 2. 启动后端

```bash
cd dailytask/backend

# 方式一：命令行
mvn spring-boot:run

# 方式二：IDEA
# 运行 BackendApplication 类
```

### 3. 启动前端

```bash
cd dailytask/frontend
npm install
npm run dev
```

访问 http://localhost:5173

### 4. 配置 AI（可选）

如需使用 AI 功能，配置 DeepSeek API Key：

**方式一：直接修改 application.yaml**

```yaml
spring:
  ai:
    openai:
      api-key: sk-your-api-key-here  # 改成自己的
```

**方式二：使用本地配置（推荐）**

创建 `application-local.yaml`：
```yaml
spring:
  ai:
    openai:
      api-key: sk-your-api-key-here
```

启动时添加参数：
```
-Dspring.profiles.active=local
```

获取 API Key：https://platform.deepseek.com/

## 接口文档

### 用户接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/user/register | 用户注册 |
| POST | /api/user/login | 用户登录 |

### 商品接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/product/list | 商品列表 |
| GET | /api/product/{id} | 商品详情 |
| GET | /api/product/search | 商品搜索 |

### 订单接口（需登录）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/order/create | 创建订单 |
| GET | /api/order/my | 我的订单 |
| PUT | /api/order/pay/{id} | 支付订单 |
| PUT | /api/order/cancel/{id} | 取消订单 |
| PUT | /api/order/confirm/{id} | 确认收货 |

### 购物车接口（需登录）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/cart/list | 购物车列表 |
| POST | /api/cart/add | 添加购物车 |
| PUT | /api/cart/update | 更新数量 |
| DELETE | /api/cart/delete/{id} | 删除商品 |

### 管理后台接口（需管理员权限）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/admin/product/list | 商品管理列表 |
| POST | /api/admin/product/add | 新增商品 |
| PUT | /api/admin/product/update | 修改商品 |
| PUT | /api/admin/product/status | 商品上下架 |
| DELETE | /api/admin/product/delete/{id} | 删除商品 |
| GET | /api/admin/order/list | 订单管理列表 |
| PUT | /api/admin/order/status | 订单状态变更 |
| GET | /api/admin/stats/overview | 数据概览 |
| GET | /api/admin/stats/orderTrend | 订单趋势 |
| GET | /api/admin/stats/categorySales | 分类销售 |

### AI 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/ai/query | 智能查询（自然语言 → SQL） |
| POST | /api/ai/chat | AI 对话 |

## 数据库表结构

| 表名 | 说明 | 数据量 |
|------|------|--------|
| user | 用户表 | 3条 |
| product | 商品表 | 10条 |
| orders | 订单表 | 4条 |
| cart | 购物车表 | - |
| category | 分类表 | 6条 |
| review | 评价表 | 8条 |

## 常见问题

**Q: AI 功能报错 401？**
A: 未配置 API Key，请参考"配置 AI"章节。

**Q: 数据库连接失败？**
A: 检查 MySQL 是否启动，用户名密码是否正确（默认 root/123456）。

**Q: 前端请求 404？**
A: 确认后端已启动，Vite 代理配置正确。

## License

MIT
