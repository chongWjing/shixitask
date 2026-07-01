# 花之恋电商平台 - 完整测试流程

## 测试前准备

### 1. 环境检查
```bash
# 检查 MySQL 是否运行
netstat -an | grep 3306

# 检查后端是否运行
curl -s http://localhost:8080/api/product/list | head -100

# 检查前端是否运行
curl -s http://localhost:5173 | head -20
```

### 2. 数据库检查
```sql
-- 连接数据库
mysql -u root -p123456

-- 检查数据库
USE shopaisx;
SHOW TABLES;
SELECT COUNT(*) FROM user;
SELECT COUNT(*) FROM product;
SELECT COUNT(*) FROM orders;
SELECT COUNT(*) FROM category;
SELECT COUNT(*) FROM review;
```

---

## 一、用户模块测试

### 1.1 用户注册
```bash
curl -s -X POST "http://localhost:8080/api/user/register" \
  -H "Content-Type: application/json; charset=UTF-8" \
  -d '{"username":"newuser","password":"123456","phone":"13800001234"}'

# 预期结果：{"code":200,"message":"success","data":"注册成功"}
```

### 1.2 用户登录
```bash
# 普通用户登录
curl -s -X POST "http://localhost:8080/api/user/login" \
  -H "Content-Type: application/json; charset=UTF-8" \
  -d '{"username":"test","password":"123456"}'

# 管理员登录
curl -s -X POST "http://localhost:8080/api/user/login" \
  -H "Content-Type: application/json; charset=UTF-8" \
  -d '{"username":"admin","password":"123456"}'

# 预期结果：返回 token 和 user 信息
```

### 1.3 获取 Token
```bash
# 保存 token 供后续测试使用
TOKEN=$(curl -s -X POST "http://localhost:8080/api/user/login" \
  -H "Content-Type: application/json; charset=UTF-8" \
  -d '{"username":"test","password":"123456"}' | \
  grep -o '"token":"[^"]*"' | cut -d'"' -f4)

echo "Token: $TOKEN"
```

---

## 二、商品模块测试

### 2.1 商品列表
```bash
curl -s "http://localhost:8080/api/product/list?page=1&size=10"

# 预期结果：返回商品列表，total=10
```

### 2.2 商品详情
```bash
curl -s "http://localhost:8080/api/product/1"

# 预期结果：返回 id=1 的商品详细信息
```

### 2.3 商品搜索
```bash
curl -s "http://localhost:8080/api/product/search?keyword=玫瑰&page=1&size=5"

# 预期结果：返回包含"玫瑰"的商品
```

### 2.4 分类列表
```bash
curl -s "http://localhost:8080/api/category/list"

# 预期结果：返回 6 个分类
```

---

## 三、订单模块测试

### 3.1 创建订单
```bash
curl -s -X POST "http://localhost:8080/api/order/create" \
  -H "Content-Type: application/json; charset=UTF-8" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"productId":1,"quantity":2,"receiverName":"test","receiverPhone":"13900001111","receiverAddress":"test address 123"}'

# 预期结果：订单创建成功，返回订单信息
```

### 3.2 我的订单
```bash
curl -s "http://localhost:8080/api/order/my?page=1&size=5" \
  -H "Authorization: Bearer $TOKEN"

# 预期结果：返回当前用户的订单列表
```

### 3.3 支付订单
```bash
# 先查看订单状态
/d/MySQL/bin/mysql.exe -u root -p123456 -e "USE shopaisx; SELECT id, status FROM orders WHERE user_id=2;"

# 支付待付款订单
curl -s -X PUT "http://localhost:8080/api/order/pay/{订单ID}" \
  -H "Authorization: Bearer $TOKEN"

# 预期结果：付款成功
```

### 3.4 确认收货
```bash
# 确认已发货的订单
curl -s -X PUT "http://localhost:8080/api/order/confirm/{订单ID}" \
  -H "Authorization: Bearer $TOKEN"

# 预期结果：确认收货成功
```

### 3.5 取消订单
```bash
curl -s -X PUT "http://localhost:8080/api/order/cancel/{订单ID}" \
  -H "Authorization: Bearer $TOKEN"

# 预期结果：取消成功
```

---

## 四、购物车模块测试

### 4.1 添加购物车
```bash
curl -s -X POST "http://localhost:8080/api/cart/add" \
  -H "Content-Type: application/json; charset=UTF-8" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"productId":3,"quantity":1}'

# 预期结果：添加成功
```

### 4.2 购物车列表
```bash
curl -s "http://localhost:8080/api/cart/list" \
  -H "Authorization: Bearer $TOKEN"

# 预期结果：返回购物车商品列表
```

### 4.3 更新数量
```bash
curl -s -X PUT "http://localhost:8080/api/cart/update" \
  -H "Content-Type: application/json; charset=UTF-8" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"id":1,"quantity":3}'

# 预期结果：更新成功
```

### 4.4 删除商品
```bash
curl -s -X DELETE "http://localhost:8080/api/cart/delete/1" \
  -H "Authorization: Bearer $TOKEN"

# 预期结果：删除成功
```

---

## 五、评价模块测试

### 5.1 评价列表
```bash
curl -s "http://localhost:8080/api/review/list?productId=1&page=1&size=5"

# 预期结果：返回商品的评价列表
```

---

## 六、管理后台测试

### 6.1 获取管理员 Token
```bash
ADMIN_TOKEN=$(curl -s -X POST "http://localhost:8080/api/user/login" \
  -H "Content-Type: application/json; charset=UTF-8" \
  -d '{"username":"admin","password":"123456"}' | \
  grep -o '"token":"[^"]*"' | cut -d'"' -f4)

echo "Admin Token: $ADMIN_TOKEN"
```

### 6.2 商品管理
```bash
# 商品列表
curl -s "http://localhost:8080/api/admin/product/list?page=1&size=5" \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# 新增商品
curl -s -X POST "http://localhost:8080/api/admin/product/add" \
  -H "Content-Type: application/json; charset=UTF-8" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{"name":"test flower","price":99.00,"originalPrice":129.00,"description":"test desc","category":"hua shu","stock":50,"status":1}'

# 修改商品状态
curl -s -X PUT "http://localhost:8080/api/admin/product/status" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{"id":11,"status":0}'
```

### 6.3 订单管理
```bash
curl -s "http://localhost:8080/api/admin/order/list?page=1&size=5" \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# 预期结果：返回所有订单列表
```

### 6.4 数据统计
```bash
# 数据概览
curl -s "http://localhost:8080/api/admin/stats/overview" \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# 订单趋势
curl -s "http://localhost:8080/api/admin/stats/orderTrend" \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# 分类销售
curl -s "http://localhost:8080/api/admin/stats/categorySales" \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

---

## 七、AI 功能测试

### 7.1 AI 智能查询
```bash
curl -s -X POST "http://localhost:8080/api/ai/query" \
  -H "Content-Type: application/json; charset=UTF-8" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"question":"how many products"}'

# 预期结果：AI 生成 SQL 并返回查询结果
# 注意：需要配置 DEEPSEEK_API_KEY
```

### 7.2 AI 对话
```bash
curl -s -X POST "http://localhost:8080/api/ai/chat" \
  -H "Content-Type: application/json; charset=UTF-8" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"message":"hello"}'

# 预期结果：AI 返回对话响应
# 注意：需要配置 DEEPSEEK_API_KEY
```

---

## 八、前端页面测试

### 8.1 商城页面
- [ ] 首页：http://localhost:5173/#/shop
- [ ] 商品详情：http://localhost:5173/#/shop/detail/1
- [ ] 购物车：http://localhost:5173/#/shop/cart
- [ ] 下单页面：http://localhost:5173/#/shop/order
- [ ] 我的订单：http://localhost:5173/#/shop/orders
- [ ] 个人中心：http://localhost:5173/#/shop/profile
- [ ] 登录页面：http://localhost:5173/#/shop/login
- [ ] 评价列表：http://localhost:5173/#/shop/review

### 8.2 管理后台页面
- [ ] 后台登录：http://localhost:5173/#/admin/login
- [ ] 仪表盘：http://localhost:5173/#/admin/dashboard
- [ ] 商品管理：http://localhost:5173/#/admin/product
- [ ] 订单管理：http://localhost:5173/#/admin/order
- [ ] AI 查询：http://localhost:5173/#/admin/ai

---

## 九、权限测试

### 9.1 未登录访问受保护页面
```bash
# 未登录访问购物车
curl -s "http://localhost:8080/api/cart/list"
# 预期结果：401 未授权
```

### 9.2 普通用户访问管理后台
```bash
# 普通用户尝试访问管理接口
curl -s "http://localhost:8080/api/admin/product/list" \
  -H "Authorization: Bearer $TOKEN"
# 预期结果：403 禁止访问
```

---

## 十、数据库验证

### 10.1 检查数据完整性
```sql
-- 检查外键关系
SELECT o.id, o.user_id, u.username 
FROM orders o 
LEFT JOIN user u ON o.user_id = u.id;

-- 检查商品库存
SELECT id, name, stock, sales FROM product;

-- 检查订单状态分布
SELECT status, COUNT(*) as count FROM orders GROUP BY status;
```

---

## 测试结果记录

| 模块 | 测试项 | 状态 | 备注 |
|------|--------|------|------|
| 用户 | 注册 | ☐ | |
| 用户 | 登录 | ☐ | |
| 商品 | 列表 | ☐ | |
| 商品 | 详情 | ☐ | |
| 商品 | 搜索 | ☐ | |
| 订单 | 创建 | ☐ | |
| 订单 | 支付 | ☐ | |
| 订单 | 确认 | ☐ | |
| 订单 | 取消 | ☐ | |
| 购物车 | 添加 | ☐ | |
| 购物车 | 列表 | ☐ | |
| 管理 | 商品管理 | ☐ | |
| 管理 | 订单管理 | ☐ | |
| 管理 | 数据统计 | ☐ | |
| AI | 智能查询 | ☐ | |
| AI | 对话 | ☐ | |
| 前端 | 商城页面 | ☐ | |
| 前端 | 后台页面 | ☐ | |
| 权限 | 访问控制 | ☐ | |

---

## 常见问题排查

### 问题 1：数据库连接失败
- 检查 MySQL 是否启动：`netstat -an | grep 3306`
- 检查用户名密码：`application.yaml` 中的 `spring.datasource.username` 和 `password`

### 问题 2：AI 功能报错 401
- 检查 API Key：`application.yaml` 中的 `spring.ai.openai.api-key`
- 或创建 `application-local.yaml` 配置本地 Key

### 问题 3：前端请求 404
- 检查后端是否启动
- 检查 Vite 代理配置

### 问题 4：订单状态错误
- 检查数据库中的订单状态：`SELECT id, status FROM orders;`
- 状态值：0=待付款，1=已付款，2=已发货，3=已完成，4=已取消

---

## 测试执行命令

```bash
# 一键测试所有接口
bash test_all.sh
```
