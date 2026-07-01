# 🌸 花之恋鲜花电商平台

> 基于 Spring Boot 3 + Spring AI + Vue 3 的鲜花电商全栈项目

## 项目简介

花之恋是一个鲜花电商平台，包含用户端商城和管理后台。集成 Spring AI + DeepSeek 大模型，支持智能搜索、自然语言查询数据库，以及丰富的 AI 能力演示（多模型对话、流式输出、MCP工具调用等）。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.5.15 + MyBatis-Plus 3.5.9 + MySQL 8.0 |
| 前端 | Vue 3 + Element Plus + Vite |
| AI | Spring AI 1.0.0 + DeepSeek（V3/R1双模型） |
| 认证 | JWT + MD5 密码加密 |

## 项目结构

```
实习作业/
├── dailytask/                          # 核心项目代码
│   ├── backend/                        # 后端 Spring Boot 项目
│   │   └── src/main/java/com/lbya/backend/
│   │       ├── config/                # 配置类（CORS、JWT拦截器、AI、多模型）
│   │       ├── controller/            # 控制器（23个）
│   │       │   ├── UserController          # 用户登录/注册/信息/退出
│   │       │   ├── ProductController       # 商品列表/详情/搜索
│   │       │   ├── OrdersController        # 订单创建/查询/支付/取消/确认
│   │       │   ├── CartController          # 购物车增删改查
│   │       │   ├── ReviewController        # 评价展示
│   │       │   ├── CategoryController      # 分类列表
│   │       │   ├── AdminProductController  # 管理员商品管理
│   │       │   ├── AdminOrderController    # 管理员订单管理
│   │       │   ├── AdminStatsController    # 管理后台统计
│   │       │   ├── AiController            # AI智能查询（NL→SQL）
│   │       │   ├── SimpleChatController    # AI基础对话
│   │       │   ├── StreamChatController    # Flux流式输出
│   │       │   ├── AiChatController        # AI对话（同步+流式）
│   │       │   ├── AiChatStreamController  # 高级流式输出
│   │       │   ├── SseEmitterChatController# SseEmitter流式
│   │       │   ├── SSEChatController       # SSE自定义事件
│   │       │   ├── MemoryChatController    # 多轮对话记忆
│   │       │   ├── RoleChatController      # 多角色对话
│   │       │   ├── MultiModelController    # 多模型路由
│   │       │   ├── StructuredController    # 结构化输出
│   │       │   ├── ModeController          # Prompt模板
│   │       │   ├── RtfPromptController     # RTF框架
│   │       │   └── McpTestController       # MCP工具调用
│   │       ├── entity/                # 实体类（User/Product/Orders/Cart/Review/Category）
│   │       ├── mapper/                # MyBatis Mapper（6个接口+6个XML）
│   │       ├── service/               # 业务逻辑层（8个接口+7个实现）
│   │       ├── exception/             # 全局异常处理
│   │       └── util/                  # 工具类（JWT/MD5/Result/PageResult）
│   │   └── src/main/resources/
│   │       ├── application.yaml       # 主配置
│   │       ├── application-local.yaml # 本地API Key配置（gitignore）
│   │       ├── sql/init.sql           # 数据库初始化脚本
│   │       └── com/lbya/backend/mapper/*.xml  # MyBatis映射文件
│   ├── frontend/                      # 前端 Vue 3 项目
│   │   └── src/
│   │       ├── api/                   # API接口封装（6个模块）
│   │       │   ├── request.js         # Axios封装（拦截器+token）
│   │       │   ├── user.js            # 用户接口
│   │       │   ├── product.js         # 商品接口
│   │       │   ├── order.js           # 订单接口
│   │       │   ├── cart.js            # 购物车接口
│   │       │   └── admin.js           # 管理后台接口
│   │       ├── router/index.js        # 路由配置（19条路由+守卫）
│   │       └── views/
│   │           ├── shop/              # 商城页面（9个）
│   │           │   ├── ShopLayout.vue     # 商城布局（导航/搜索/页脚）
│   │           │   ├── Home.vue           # 首页（轮播/分类/商品/评价）
│   │           │   ├── ProductDetail.vue  # 商品详情
│   │           │   ├── ShopCart.vue       # 购物车
│   │           │   ├── Orders.vue         # 下单确认
│   │           │   ├── MyOrders.vue       # 我的订单
│   │           │   ├── Profile.vue        # 个人中心
│   │           │   ├── Login.vue          # 登录/注册
│   │           │   └── ReviewList.vue     # 评价列表
│   │           └── admin/             # 后台页面（6个）
│   │               ├── Layout.vue         # 后台布局（侧边栏+导航）
│   │               ├── Login.vue          # 管理员登录
│   │               ├── Dashboard.vue      # 数据概览
│   │               ├── ProductManage.vue  # 商品管理
│   │               ├── OrderManage.vue    # 订单管理
│   │               └── AiQuery.vue        # AI智能查询
│   ├── test_all.sh                    # 自动化测试脚本（20个API测试）
│   └── TEST_PLAN.md                   # 测试计划文档
├── day1~day10/                        # 10天课程资料
│   ├── day1/                          # Spring AI基础（同步调用）
│   ├── day2/                          # 流式输出与前端AI
│   ├── day3/                          # MCP工具调用+多模型
│   └── day10/电商平台技术手册/          # 完整技术手册（14章）
├── 原型图/                             # Figma导出的HTML原型
│   ├── shop-home.html                 # 商城首页原型
│   └── shop-login.html                # 登录页原型
└── README.md
```

## 功能特性

### 🛒 用户端商城
- 首页轮播图 + 6分类导航（鲜花/永生花/花束/花篮/绿植/礼品花），点击分类标题动态切换
- 商品列表（分页、关键词搜索、分类筛选、骨架屏加载）
- 商品详情（图片预览、价格折扣、库存限制数量选择）
- 购物车管理（添加/修改数量/删除/批量操作，数量实时同步后端）
- 下单确认（收货信息表单、价格汇总、购物车结算/直接购买两种模式）
- 下单后自动清除购物车已购商品
- 订单管理（待付款/已付款/已发货/已完成/已取消 状态筛选）
- 模拟支付 / 确认收货
- 用户注册 / 登录 / 个人中心
- 加购和购买前自动检查登录状态
- 退出登录二次确认弹窗

### 🔧 管理后台
- 数据仪表盘（商品总数、今日订单、总销售额、用户总数）
- 7天订单趋势图 + 分类销售统计
- 商品管理（新增/编辑/删除/上下架）
- 订单管理（列表筛选/发货/完成）
- AI 智能查询（自然语言 → SQL，5层安全校验）

### 🔄 实时同步
- 用户下单后管理员切回标签页自动刷新订单列表
- 管理员发货后用户切回标签页自动更新订单状态

### 🤖 Spring AI 功能（Day 1-3 课程作业）
- 简单对话 / Token统计 / 异常处理 / 模型参数覆盖
- System Prompt / 参数化模板
- Flux 流式输出 / SseEmitter 方案
- SSE 自定义事件类型
- ChatMemory 多轮对话记忆（20条消息窗口）
- 多角色对话（宿管助手 vs 辅导员）
- 多模型路由（V3快速 + R1推理 + 智能路由）
- 结构化输出（AI → Java对象）
- RTF Prompt 框架
- MCP 工具调用（计算器/邮件等工具）
- NL→SQL 智能表格查询

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

**数据库名：** `shopaisx`

### 2. 配置 AI（可选）

如需使用 AI 功能，配置 DeepSeek API Key：

**方式一：直接修改 application.yaml**

```yaml
spring:
  ai:
    openai:
      api-key: sk-your-api-key-here
```

**方式二：使用本地配置（推荐，不提交到Git）**

创建 `dailytask/backend/src/main/resources/application-local.yaml`：
```yaml
spring:
  ai:
    openai:
      api-key: sk-your-api-key-here
```

启动时添加参数：`-Dspring.profiles.active=local`

获取 API Key：https://platform.deepseek.com/

### 3. 启动后端

```bash
cd dailytask/backend
mvn spring-boot:run
```

或在 IDEA 中运行 `BackendApplication` 类（**必须加** `--spring.profiles.active=local`）。

启动成功后控制台会显示：
```
╔══════════════════════════════════════════════════════╗
║          🌸 花之恋鲜花电商平台 启动成功！              ║
╠══════════════════════════════════════════════════════╣
║  🛒 商城前台：  http://localhost:5173/#/shop           ║
║  👤 用户登录：  http://localhost:5173/#/shop/login     ║
║  🔧 管理后台：  http://localhost:5173/#/admin/login    ║
║  📋 管理员：admin / 123456                             ║
║  📋 普通用户：test / 123456                            ║
╚══════════════════════════════════════════════════════╝
```

### 4. 启动前端

```bash
cd dailytask/frontend
npm install
npm run dev
```

访问 http://localhost:5173

### 5. 测试账号

| 用户名 | 密码 | 角色 | 用途 |
|--------|------|------|------|
| admin | 123456 | 管理员 | 管理后台 |
| test | 123456 | 普通用户 | 商城购物 |
| lily | 123456 | 普通用户 | 商城购物 |

## 接口文档

### 用户接口

| 方法 | 路径 | 说明 | 需登录 |
|------|------|------|--------|
| POST | /api/user/register | 用户注册 | 否 |
| POST | /api/user/login | 用户登录 | 否 |
| GET | /api/user/info | 获取用户信息 | 是 |
| POST | /api/user/logout | 退出登录 | 是 |

### 商品接口

| 方法 | 路径 | 说明 | 需登录 |
|------|------|------|--------|
| GET | /api/product/list?page=1&size=10 | 商品列表（分页） | 否 |
| GET | /api/product/{id} | 商品详情 | 否 |
| GET | /api/product/search?keyword=xxx | 商品搜索 | 否 |
| GET | /api/category/list | 分类列表 | 否 |

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
| DELETE | /api/cart/clear | 清空购物车 |

### 评价接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/review/latest | 最新评价 |
| GET | /api/review/list | 全部评价 |

### 管理后台接口（需管理员权限）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/admin/product/list | 商品管理列表 |
| POST | /api/admin/product/add | 新增商品 |
| PUT | /api/admin/product/update | 修改商品 |
| PUT | /api/admin/product/status | 商品上下架 |
| DELETE | /api/admin/product/delete/{id} | 删除商品（逻辑删除） |
| GET | /api/admin/order/list | 订单管理列表 |
| PUT | /api/admin/order/status | 订单状态变更 |
| GET | /api/admin/stats/overview | 数据概览 |
| GET | /api/admin/stats/orderTrend | 7天订单趋势 |
| GET | /api/admin/stats/categorySales | 分类销售统计 |

### AI 接口

| 方法 | 路径 | 说明 | 需管理员 |
|------|------|------|----------|
| POST | /api/ai/query | 智能查询（自然语言→SQL） | 是 |
| POST | /api/aichat | AI对话（同步） | 否 |
| GET | /api/aichat/stream | AI对话（流式SSE） | 否 |

### Spring AI 演示接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /chat?message=xxx | 简单对话 |
| GET | /chat/info?message=xxx | 对话+Token统计 |
| GET | /chat/safe?message=xxx | 异常处理演示 |
| GET | /chat/strict?message=xxx | 模型参数覆盖 |
| GET | /mode/system?question=xxx | System Prompt |
| GET | /mode/template?building=&student= | 参数化模板 |
| GET | /memory/chat?conversationId=&message= | 多轮记忆对话 |
| GET | /struct/student?desc=xxx | 结构化输出 |
| GET | /role/dormitory?message=xxx | 宿管助手 |
| GET | /role/tutor?message=xxx | 辅导员角色 |
| GET | /role/compare?message=xxx | 角色对比 |
| GET | /rtf/chat?message=xxx | RTF Prompt框架 |
| GET | /chat/stream?message=xxx | Flux流式输出 |
| GET | /sse-emitter/chat?message=xxx | SseEmitter流式 |
| GET | /api/sse/repair-progress?repairId= | SSE自定义事件 |
| GET | /api/aichat/stream-v2?message=xxx | 高级流式输出 |
| GET | /mcp/chat?message=xxx | MCP工具调用 |
| GET | /model/v3?message=xxx | V3模型对话 |
| GET | /model/r1?message=xxx | R1推理模型 |
| GET | /model/smart?message=xxx | 智能路由 |

## 数据库表结构

| 表名 | 说明 | 当前数据量 |
|------|------|-----------|
| user | 用户表（含管理员） | 8 条 |
| product | 商品表 | 21 条 |
| orders | 订单表 | 40 条 |
| cart | 购物车表 | 3 条 |
| category | 分类表 | 6 条 |
| review | 评价表 | 13 条 |

**订单状态码：** 0=待付款 1=已付款 2=已发货 3=已完成 4=已取消

**商品状态：** 0=下架 1=上架

**用户角色：** 0=普通用户 1=管理员

## 技术手册

`day10/电商平台技术手册/` 包含14章完整技术文档：

| 章节 | 内容 | 对应代码 |
|------|------|----------|
| 第1章 | 课程前言与环境搭建 | 数据库初始化 |
| 第2章 | Figma原型 + Vue3项目初始化 | 前端脚手架 |
| 第3章 | 商城首页轮播与导航 | ShopLayout + Home |
| 第4章 | 商品列表与详情页 | Home + ProductDetail |
| 第5章 | 下单页面与登录 | Orders + Login + MyOrders |
| 第6章 | SpringBoot项目搭建 | 后端骨架 + Entity + Mapper |
| 第7章 | 用户登录/退出接口 | UserController + JWT + 拦截器 |
| 第8章 | 商品与订单接口 | ProductController + OrderController |
| 第9章 | 管理接口 | AdminProduct + AdminOrder |
| 第10章 | 前后端联调 | API模块 + 真实数据对接 |
| 第11章 | 后台管理员登录与主页 | Admin Login + Dashboard |
| 第12章 | 后台商品与订单管理 | ProductManage + OrderManage |
| 第13章 | Spring AI集成 | AiController(NL→SQL) |
| 第14章 | AI代码生成与验收 | 全项目验收 |

## 常见问题

**Q: 启动报错 "OpenAI API key must be set"？**
A: 没有配置 API Key 或没加 `--spring.profiles.active=local` 参数。

**Q: 数据库连接失败？**
A: 检查 MySQL 是否启动，数据库名是否为 `shopaisx`，用户名密码是否为 root/123456。

**Q: 前端请求 404？**
A: 确认后端已启动（端口8080），Vite 代理配置正确（`/api` → `localhost:8080`）。

**Q: AI智能查询报错？**
A: 需要管理员登录后才能使用，且需配置 DeepSeek API Key。

**Q: 前端页面白屏？**
A: 检查路由是否配置正确，打开浏览器 F12 查看控制台报错。

**Q: 搜索没有结果？**
A: 数据库已建全文索引（FULLTEXT + ngram），确认 MySQL 版本支持。

**Q: 加入购物车提示未登录？**
A: 请先登录后再操作，加购和购买都会自动检查登录状态。

**Q: 管理员发货后用户订单没更新？**
A: 切换回用户端浏览器标签页，订单状态会自动刷新，无需手动刷新页面。

## License

MIT
