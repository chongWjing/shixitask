# 花之恋商城前端

> Spring AI + Vue3 智能鲜花电商平台（前端）

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue 3 | 3.x | 前端框架 |
| Vite | 5.x | 构建工具 |
| Vue Router | 4.x | 路由管理 |
| Element Plus | 2.x | UI 组件库 |
| Axios | 1.x | HTTP 请求 |

---

## 运行步骤

### 1. 安装依赖

```bash
cd frontend
npm install
```

### 2. 启动前端

```bash
npm run dev
```

浏览器访问 `http://localhost:5173`

### 3. 启动后端

在 IDEA 中运行 `dailytask` 项目的 `SmartApplication.java`

前端通过 Vite proxy 自动将 `/api` 请求代理到 `http://localhost:8080`

---

## 页面路由

| 路径 | 页面 | 需登录 |
|------|------|--------|
| `/shop` | 商城首页 | ❌ |
| `/shop/login` | 登录/注册 | ❌ |
| `/shop/product/:id` | 商品详情 | ❌ |
| `/shop/cart` | 购物车 | ✅ |
| `/shop/order/:id` | 订单详情 | ✅ |
| `/shop/my-orders` | 我的订单 | ✅ |
| `/shop/user` | 个人中心 | ✅ |

---

## 后端接口

| 方法 | 路径 | 说明 | 需登录 |
|------|------|------|--------|
| POST | `/user/login` | 用户登录 | ❌ |
| POST | `/user/register` | 用户注册 | ❌ |
| POST | `/user/logout` | 退出登录 | ✅ |
| GET | `/user/info` | 获取当前用户信息 | ✅ |
| PUT | `/user/profile` | 更新手机号 | ✅ |
| PUT | `/user/password` | 修改密码 | ✅ |

---

## 项目结构

```
frontend/
├── package.json
├── vite.config.js                 ← Vite 配置：Element Plus 自动导入 + API proxy
├── index.html
└── src/
    ├── main.js                    ← 入口：Vue + Router + Element Plus
    ├── App.vue                    ← 根组件：<router-view />
    ├── api/
    │   ├── request.js             ← axios 封装：baseURL=/api + 响应拦截器
    │   └── user.js                ← 用户 API：login/register/logout/getUserInfo
    ├── router/
    │   └── index.js               ← 路由配置 + beforeEach 登录守卫
    └── views/shop/
        ├── ShopLayout.vue         ← 布局：顶栏 + 导航 + 内容区 + Footer
        ├── Login.vue              ← 登录/注册页（花之恋红色渐变主题）
        ├── Home.vue               ← 商城首页（分类 + 商品 + 评价）
        ├── ProductDetail.vue      ← 商品详情（占位）
        ├── Cart.vue               ← 购物车（占位）
        ├── Order.vue              ← 订单详情（占位）
        ├── MyOrders.vue           ← 我的订单（占位）
        └── UserCenter.vue         ← 个人中心（占位）
```

---

## 登录流程

```
用户输入账号密码
  → 前端调用 POST /user/login
  → 后端验证密码（MD5 比对）
  → 返回 { token: "Bearer xxx", user: { id, username, role, phone } }
  → 前端存储 token/username/role 到 localStorage
  → 顶栏动态切换为 "Hi, 用户名 | 退出"
```

## 测试账号

| 用户名 | 密码 | 说明 |
|--------|------|------|
| test | 123456 | 预置测试用户 |

可在注册页创建新用户，密码以 MD5 加密存储。

---

## 开发说明

- **跨域**：Vite proxy 解决 5173→8080 跨域，无需后端额外配置
- **token 传递**：axios 拦截器自动在请求头加 `Authorization: Bearer <token>`
- **401 处理**：axios 响应拦截器统一处理，组件内 catch 写空块即可
- **占位页面**：ProductDetail/Cart/Order/MyOrders/UserCenter 待后续实现
