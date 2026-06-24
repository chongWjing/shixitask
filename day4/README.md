# Day4 - 前端开发与全栈联调

## 任务目标

实现 Vue3 前端页面 + Spring Boot 用户管理后端。

## 完成内容

### 前端（frontend/）

| 页面 | 功能 |
|------|------|
| `Login.vue` | 登录/注册（表单校验、Token 持久化） |
| `Home.vue` | 首页（分类、商品列表、评价） |
| `ShopLayout.vue` | 布局组件（顶部导航、底部版权） |
| `ProductDetail.vue` | 商品详情（占位） |
| `Cart.vue` | 购物车（占位） |
| `Order.vue` | 订单（占位） |
| `MyOrders.vue` | 我的订单（占位） |
| `UserCenter.vue` | 个人中心（占位） |

### 后端（dailytask/）

| 文件 | 功能 |
|------|------|
| `UserController.java` | 登录/注册/登出/用户信息 |
| `UserService.java` | 用户服务接口 |
| `UserServiceImpl.java` | 用户服务实现（MD5 + Token） |
| `AuthInterceptor.java` | JWT Token 拦截器 |
| `TokenStore.java` | Token 存储（内存） |
| `WebConfig.java` | CORS 跨域 + 拦截器注册 |

### 设计稿（day4/ + 原型图/）

- `LoginPage.fig` - Figma 登录页设计
- `CartPage.fig` - Figma 购物车设计
- `DetailPage.fig` - Figma 商品详情设计
- `Odering.fig` - Figma 下单页设计
- `OdersPage.fig` - Figma 订单页设计
- `原型图/shop-login.html` - 登录页 HTML 原型
- `原型图/shop-home.html` - 首页 HTML 原型

## 技术点

- Vue3 Options API + Element Plus
- Axios 封装 + 响应拦截器
- Vite 代理（`rewrite: path.replace(/^\/api/, '')`）
- 路由守卫（`router.beforeEach`）
- JWT + MD5 密码加密

## 运行方式

```bash
# 启动后端
cd dailytask
mvn spring-boot:run

# 启动前端
cd frontend
npm install
npm run dev

# 浏览器访问 http://localhost:5173
# 测试账号: test / 123456
```

## 前后端联调

前端请求 `http://localhost:5173/api/user/login` → Vite 代理 → 后端 `http://localhost:8080/user/login`

**关键配置** (`vite.config.js`):
```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    rewrite: (path) => path.replace(/^\/api/, '')
  }
}
```
