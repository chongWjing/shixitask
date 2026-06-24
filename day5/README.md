# Day5 - Spring Boot 电商后端

## 任务目标

搭建完整的电商后端 API，包含用户、商品、订单、购物车等 6 张表。

## 项目结构

```
day5/
├── src/main/java/com/huashan/backend/
│   ├── BackendApplication.java          # 启动类（@MapperScan）
│   ├── config/
│   │   ├── WebMvcConfig.java            # CORS + 拦截器注册
│   │   └── LoginInterceptor.java        # JWT 认证拦截器
│   ├── controller/
│   │   ├── UserController.java          # /api/user/login, /register
│   │   └── ProductController.java       # /api/product/list
│   ├── entity/                          # 6 个实体类
│   │   ├── User.java
│   │   ├── Product.java
│   │   ├── Orders.java
│   │   ├── Cart.java
│   │   ├── Category.java
│   │   └── Review.java
│   ├── mapper/                          # 6 个 Mapper 接口 + XML
│   ├── service/                         # 6 个 Service 接口
│   └── util/
│       ├── JwtUtil.java                 # JWT 生成/解析
│       ├── Md5Util.java                 # MD5 加密
│       ├── Result.java                  # 统一响应封装
│       └── PageResult.java              # 分页结果封装
└── src/main/resources/
    ├── application.yaml                 # 数据库 + MyBatis 配置
    └── com/huashan/backend/mapper/      # 6 个 XML 映射文件
```

## 数据库表

| 表名 | 说明 |
|------|------|
| user | 用户表 |
| product | 商品表 |
| orders | 订单表 |
| cart | 购物车表 |
| category | 分类表 |
| review | 评价表 |

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.15 | 基础框架 |
| MyBatis-Plus | 3.5.9 | ORM 框架 |
| java-jwt | 4.4.0 | JWT 认证 |
| MySQL | 8.x | 数据库 |
| Lombok | - | 简化代码 |

## API 接口

### 用户

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/user/login` | 登录 |
| POST | `/api/user/register` | 注册 |

### 商品

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/product/list` | 商品列表（分页+分类） |

## 运行方式

```bash
# 1. 创建数据库
mysql -u root -p -e "CREATE DATABASE ecommerce"

# 2. 导入表结构（使用 day5/sql/ 下的 SQL 文件）

# 3. 修改配置
# src/main/resources/application.yaml
# spring.datasource.username: your-username
# spring.datasource.password: your-password

# 4. 启动项目
mvn spring-boot:run

# 5. 测试
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```
