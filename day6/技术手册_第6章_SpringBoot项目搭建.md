# 第6章 SpringBoot项目搭建 + JSON数据结构

> **学习目标：** 创建SpringBoot 3.5.3项目，配置数据库连接，编写统一响应类和跨域配置，完成实体类和Mapper
> **前置条件：** 完成第1章的环境搭建和数据库初始化
> **本章产出：** SpringBoot项目搭建完毕，能返回JSON数据，数据库连接正常

---

## 6.1 SpringBoot项目介绍（30分钟）

"同学们，第一周我们用Vue+ElementPlus搭建了完整的前端商城。从这周开始，我们要写后端了——用SpringBoot提供REST API。"

"SpringBoot三个核心概念——"

**（板书）**

```
SpringBoot 三大核心：

1. 自动配置（Auto Configuration）
   - 引入spring-boot-starter-web → 自动配置Tomcat、SpringMVC
   - 引入mybatis-spring-boot-starter → 自动配置SqlSessionFactory
   - 不用写XML，引入依赖就能用

2. 起步依赖（Starter Dependencies）
   - spring-boot-starter-web → SpringMVC + Tomcat + Jackson
   - spring-boot-starter-jdbc → HikariCP连接池 + JDBC
   - mybatis-spring-boot-starter → MyBatis + 自动配置
   - 一个starter搞定一组jar包

3. 内嵌Tomcat
   - 不需要部署到外部Tomcat
   - 打成jar包，java -jar 直接运行
   - 默认端口8080
```

---

### 步骤1：创建SpringBoot项目

**操作：**

1. 打开IDEA
2. File → New → Project
3. 左侧选择 "Spring Initializr"
4. 填写项目信息：
   - Name：`backend`
   - Location：`E:\HuaShan\ecommerce`
   - Language：Java
   - Type：Maven
   - Group：`com.ecommerce`
   - Artifact：`ecommerce`
   - Package name：`com.ecommerce`
   - JDK：**17**
5. 点击 Next
6. 选择依赖（Spring Boot版本选择 **3.5.3**）：
   - ✅ Spring Web
   - ✅ MyBatis Framework
   - ✅ MySQL Driver
   - ✅ Lombok
7. 点击 Create

**预期效果：** IDEA创建了一个SpringBoot 3.5.3项目，目录结构如下：

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/ecommerce/
│   │   │   └── EcommerceApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/
├── pom.xml
└── ...
```

---

### 步骤2：配置pom.xml

打开 `E:\HuaShan\ecommerce\backend\pom.xml`，替换为以下内容：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.3</version>
        <relativePath/>
    </parent>

    <groupId>com.ecommerce</groupId>
    <artifactId>ecommerce</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>ecommerce</name>
    <description>花之恋鲜花电商平台后端API</description>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <!-- Spring Web：提供SpringMVC + 内嵌Tomcat -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- MyBatis：数据库ORM框架（兼容SpringBoot 3.x） -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>3.0.4</version>
        </dependency>

        <!-- MySQL驱动 -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- JWT：生成和验证Token -->
        <dependency>
            <groupId>com.auth0</groupId>
            <artifactId>java-jwt</artifactId>
            <version>4.4.0</version>
        </dependency>

        <!-- Lombok：简化实体类代码 -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Spring Boot Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

保存后，IDEA会自动下载Maven依赖（首次可能需要几分钟）。

**说明：**
- SpringBoot 3.5.3 要求 JDK 17+，不再支持 JDK 8
- `mybatis-spring-boot-starter` 使用 3.0.4 版本，兼容 SpringBoot 3.x
- `java-jwt` 使用 4.4.0，后续第7章JWT会用到
- MySQL驱动使用 `com.mysql:mysql-connector-j`（新版artifactId）

**预期效果：** pom.xml没有红色波浪线，External Libraries中出现大量jar包

---

### 步骤3：配置application.yml

删除 `src/main/resources/application.properties`（右键→Delete），创建 `application.yml`：

**操作：** 右键 `src/main/resources` → New → File → 输入 `application.yml`

内容如下：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ecommerce?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
  ai:
    openai:
      api-key: sk-your-deepseek-api-key
      base-url: https://api.deepseek.com
      chat:
        options:
          model: deepseek-chat
          temperature: 0.7

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.ecommerce.entity
  configuration:
    map-underscore-to-camel-case: true
```

**说明：**
- `map-underscore-to-camel-case: true`：数据库字段 `product_name` 自动映射到Java属性 `productName`
- `driver-class-name: com.mysql.cj.jdbc.Driver`：SpringBoot 3.x + MySQL 8.0 使用新驱动类名（不再是 `com.mysql.jdbc.Driver`）
- `serverTimezone=Asia/Shanghai`：设置时区为上海，避免时间差8小时
- `useSSL=false&allowPublicKeyRetrieval=true`：MySQL 8.0连接参数
- `spring.ai` 配置预留给后续章节使用，目前不影响项目启动

---

### 步骤4：创建包结构

**操作：** 在 `backend/src/main/java/com/ecommerce/` 下创建以下包

```
com.ecommerce/
├── controller/       # 控制层：接收请求，返回响应
├── service/          # 服务层：业务逻辑
├── mapper/           # 数据访问层：操作数据库
├── entity/           # 实体类：对应数据库表
├── config/           # 配置类：跨域、拦截器等
└── util/             # 工具类：JWT、MD5、Result等
└── EcommerceApplication.java  # 启动类
```

可以用IDEA创建：右键 `com.ecommerce` → New → Package → 输入包名

**注意：** 统一规范中 `Result.java` 放在 `util` 包下，不是 `entity` 包。

---

### 步骤5：在启动类上添加Mapper扫描

打开 `E:\HuaShan\ecommerce\backend\src\main\java\com\ecommerce\EcommerceApplication.java`：

```java
package com.ecommerce;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ecommerce.mapper")  // 扫描Mapper接口
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }

}
```

**预期效果：** 添加 `@MapperScan` 注解后，Spring会自动扫描mapper包下的所有接口

---

### 步骤6：验证项目启动

点击IDEA右上角的绿色三角形（Run按钮），或按 Shift+F10 启动项目。

**预期效果：** 控制台输出类似以下内容：

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::               (v3.5.3)

...
Started EcommerceApplication in 2.345 seconds (JVM running for 3.456)
```

> ⚠️ 如果报错 "Communications link failure"，检查MySQL服务是否启动，用户名密码是否正确

---

## 6.2 JSON数据结构介绍（30分钟）

"项目跑起来了，接下来我们要理解一个核心概念——JSON。前后端分离架构中，JSON就是前端和后端之间的'通用语言'。"

**（板书）**

```
前后端数据交互过程：

前端(Vue)                后端(SpringBoot)
   │                         │
   │  GET /api/product/list  │  ← HTTP请求
   │ ───────────────────────>│
   │                         │  查询数据库
   │                         │  ↓
   │  {                      │
   │    "code": 200,         │  ← JSON响应
   │    "message": "操作成功",│
   │    "data": [...]        │
   │  }                      │
   │ <───────────────────────│
   │                         │
   解析JSON，渲染页面        │
```

### JSON格式示例

**对象（Object）：**

```json
{
  "id": 1,
  "name": "Apple iPhone 15 Pro Max",
  "price": 9999.00
}
```

**数组（Array）：**

```json
[
  { "id": 1, "name": "商品1" },
  { "id": 2, "name": "商品2" }
]
```

**嵌套（Nested）：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "list": [
      { "id": 1, "name": "商品1" },
      { "id": 2, "name": "商品2" }
    ],
    "total": 12
  }
}
```

### RESTful API设计规范

"我们后端的接口遵循RESTful风格——"

| 操作 | HTTP方法 | URL | 说明 |
|------|---------|-----|------|
| 查询商品列表 | GET | /api/product/list | 获取所有商品（分页） |
| 查询商品详情 | GET | /api/product/{id} | 根据ID获取单个商品 |
| 用户登录 | POST | /api/user/login | 提交用户名密码 |
| 用户注册 | POST | /api/user/register | 创建新用户 |
| 创建订单 | POST | /api/order/create | 提交订单数据 |
| 修改商品 | PUT | /api/admin/product/update | 更新商品信息 |
| 删除商品 | DELETE | /api/admin/product/delete/{id} | 删除商品 |

"统一响应格式——所有接口返回的JSON都遵循这个结构："

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

- `code`：状态码，200成功，401未登录，403无权限，500服务器错误
- `message`：提示信息
- `data`：实际数据，可以是对象、数组或null

---

## 6.3 统一响应类与跨域配置（45分钟）

### 步骤1：编写统一响应类Result

创建文件 `E:\HuaShan\ecommerce\backend\src\main\java\com\ecommerce\util\Result.java`：

```java
package com.ecommerce.util;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        result.setData(null);
        return result;
    }

    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return result;
    }
}
```

**说明：**
- Result类放在 `com.ecommerce.util` 包下，不是entity包
- 使用Lombok `@Data` 自动生成getter/setter/toString
- `code` 字段类型为 `int`（基本类型），与统一规范一致
- 四个静态方法：`success(T data)`、`success(String message, T data)`、`error(String message)`、`error(int code, String message)`

---

### 步骤2：编写全局异常处理器

创建文件 `E:\HuaShan\ecommerce\backend\src\main\java\com\ecommerce\config\GlobalExceptionHandler.java`：

```java
package com.ecommerce.config;

import com.ecommerce.util.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        e.printStackTrace();
        return Result.error("服务器内部错误：" + e.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public Result<?> handleNullPointerException(NullPointerException e) {
        e.printStackTrace();
        return Result.error("数据不存在");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        return Result.error(400, e.getMessage());
    }
}
```

**说明：** 导入 `com.ecommerce.util.Result`，不是 `com.ecommerce.entity.Result`。

---

### 步骤3：编写跨域配置

创建文件 `E:\HuaShan\ecommerce\backend\src\main\java\com\ecommerce\config\WebMvcConfig.java`：

```java
package com.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

**说明：**
- 跨域配置放在 `WebMvcConfig` 中，后续第7章会在同一个类中注册拦截器
- 使用 `allowedOriginPatterns("*")` 替代已废弃的 `allowedOrigins("*")`
- SpringBoot 3.x 不需要 import `javax.servlet`，跨域配置无需servlet相关import

---

### 步骤4：编写测试接口

创建文件 `E:\HuaShan\ecommerce\backend\src\main\java\com\ecommerce\controller\TestController.java`：

```java
package com.ecommerce.controller;

import com.ecommerce.util.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/test")
    public Result<Map<String, Object>> test() {
        Map<String, Object> data = new HashMap<>();
        data.put("project", "花之恋鲜花电商平台");
        data.put("version", "1.0.0");
        data.put("status", "running");
        return Result.success("后端服务运行正常", data);
    }
}
```

---

### 步骤5：验证测试接口

重启SpringBoot项目（如果正在运行，先停止再启动），然后在浏览器中访问：

```
http://localhost:8080/api/test
```

**预期效果：** 浏览器显示以下JSON：

```json
{
  "code": 200,
  "message": "后端服务运行正常",
  "data": {
    "project": "花之恋鲜花电商平台",
    "version": "1.0.0",
    "status": "running"
  }
}
```

---

## 6.4 实体类与数据库连接（30分钟）

### 步骤1：编写User实体类

创建文件 `E:\HuaShan\ecommerce\backend\src\main\java\com\ecommerce\entity\User.java`：

```java
package com.ecommerce.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private Integer id;

    private String username;

    private String password;

    private String phone;

    private Integer role;

    private Integer status;

    private LocalDateTime createTime;
}
```

**说明：**
- 字段与数据库 `user` 表一一对应：id, username, password, phone, role, status, create_time
- 使用Lombok `@Data`，不需要手写getter/setter
- `status` 字段：0禁用，1正常
- `role` 字段：0普通用户，1管理员

---

### 步骤2：编写Product实体类

创建文件 `E:\HuaShan\ecommerce\backend\src\main\java\com\ecommerce\entity\Product.java`：

```java
package com.ecommerce.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {

    private Integer id;

    private String name;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private String description;

    private String category;

    private String image;

    private Integer stock;

    private Integer sales;

    private Integer status;

    private LocalDateTime createTime;
}
```

**说明：**
- `originalPrice`：原价，对应数据库 `original_price` 字段
- `price` 和 `originalPrice` 使用 `BigDecimal` 类型，避免浮点数精度问题
- `category`：分类（鲜花/永生花/花束/花篮/绿植/礼品花）

---

### 步骤3：编写Order实体类

创建文件 `E:\HuaShan\ecommerce\backend\src\main\java\com\ecommerce\entity\Order.java`：

```java
package com.ecommerce.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {

    private Integer id;

    private String orderNo;

    private Integer userId;

    private Integer productId;

    private String productName;

    private String productImage;

    private BigDecimal productPrice;

    private Integer quantity;

    private BigDecimal totalPrice;

    private String receiverName;

    private String receiverPhone;

    private String receiverAddress;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
```

**说明：**
- 对应数据库 `orders` 表（用orders避免与MySQL关键字ORDER冲突）
- `receiverName`、`receiverPhone`、`receiverAddress`：收货人信息，对应数据库 `receiver_name`、`receiver_phone`、`receiver_address`
- `updateTime`：更新时间，对应数据库 `update_time`

---

### 步骤4：编写UserMapper接口

创建文件 `E:\HuaShan\ecommerce\backend\src\main\java\com\ecommerce\mapper\UserMapper.java`：

```java
package com.ecommerce.mapper;

import com.ecommerce.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findByUsername(@Param("username") String username);

    User findById(@Param("id") Integer id);

    int insert(User user);
}
```

**说明：** 使用 `@Mapper` 注解标记接口。`findByUsername` 和 `findById` 供第7章登录/查询使用。

---

### 步骤5：编写UserMapper XML

在 `src/main/resources/` 下创建 `mapper` 目录，然后创建文件 `E:\HuaShan\ecommerce\backend\src\main\resources\mapper\UserMapper.xml`：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.ecommerce.mapper.UserMapper">

    <select id="findByUsername" resultType="com.ecommerce.entity.User">
        SELECT id, username, password, phone, role, status, create_time
        FROM user
        WHERE username = #{username}
    </select>

    <select id="findById" resultType="com.ecommerce.entity.User">
        SELECT id, username, password, phone, role, status, create_time
        FROM user
        WHERE id = #{id}
    </select>

    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO user (username, password, phone, role, status)
        VALUES (#{username}, #{password}, #{phone}, #{role}, #{status})
    </insert>

</mapper>
```

**说明：** 表名为 `user`（不加 `t_` 前缀），与统一规范一致。

---

### 步骤6：编写ProductMapper接口

创建文件 `E:\HuaShan\ecommerce\backend\src\main\java\com\ecommerce\mapper\ProductMapper.java`：

```java
package com.ecommerce.mapper;

import com.ecommerce.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    List<Product> findAll();

    Product findById(@Param("id") Integer id);

    List<Product> findByPage(@Param("offset") int offset, @Param("size") int size);

    int countAll();

    int decreaseStock(@Param("id") Integer id, @Param("quantity") int quantity);

    int increaseSales(@Param("id") Integer id, @Param("quantity") int quantity);
}
```

**说明：** `findAll` 和 `findById` 是统一规范要求的基础方法，其余方法供后续章节使用。

---

### 步骤7：编写ProductMapper XML

创建文件 `E:\HuaShan\ecommerce\backend\src\main\resources\mapper\ProductMapper.xml`：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.ecommerce.mapper.ProductMapper">

    <select id="findAll" resultType="com.ecommerce.entity.Product">
        SELECT id, name, price, original_price, description, category, image, stock, sales, status, create_time
        FROM product
        WHERE status = 1
        ORDER BY sales DESC
    </select>

    <select id="findById" resultType="com.ecommerce.entity.Product">
        SELECT id, name, price, original_price, description, category, image, stock, sales, status, create_time
        FROM product
        WHERE id = #{id}
    </select>

    <select id="findByPage" resultType="com.ecommerce.entity.Product">
        SELECT id, name, price, original_price, description, category, image, stock, sales, status, create_time
        FROM product
        WHERE status = 1
        ORDER BY sales DESC
        LIMIT #{offset}, #{size}
    </select>

    <select id="countAll" resultType="int">
        SELECT COUNT(*)
        FROM product
        WHERE status = 1
    </select>

    <update id="decreaseStock">
        UPDATE product
        SET stock = stock - #{quantity}
        WHERE id = #{id} AND stock >= #{quantity}
    </update>

    <update id="increaseSales">
        UPDATE product
        SET sales = sales + #{quantity}
        WHERE id = #{id}
    </update>

</mapper>
```

**说明：** 表名为 `product`（不加 `t_` 前缀）。SELECT中包含 `original_price` 字段，与Product实体类的 `originalPrice` 对应。

---

### 步骤8：编写OrderMapper接口

创建文件 `E:\HuaShan\ecommerce\backend\src\main\java\com\ecommerce\mapper\OrderMapper.java`：

```java
package com.ecommerce.mapper;

import com.ecommerce.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    int insert(Order order);

    List<Order> findByUserId(@Param("userId") Integer userId);

    Order findById(@Param("id") Integer id);

    List<Order> findAll(@Param("offset") int offset, @Param("size") int size);

    int countAll();

    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
}
```

---

### 步骤9：编写OrderMapper XML

创建文件 `E:\HuaShan\ecommerce\backend\src\main\resources\mapper\OrderMapper.xml`：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.ecommerce.mapper.OrderMapper">

    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO orders (order_no, user_id, product_id, product_name, product_image,
                           product_price, quantity, total_price, receiver_name, receiver_phone,
                           receiver_address, status)
        VALUES (#{orderNo}, #{userId}, #{productId}, #{productName}, #{productImage},
                #{productPrice}, #{quantity}, #{totalPrice}, #{receiverName}, #{receiverPhone},
                #{receiverAddress}, #{status})
    </insert>

    <select id="findByUserId" resultType="com.ecommerce.entity.Order">
        SELECT id, order_no, user_id, product_id, product_name, product_image,
               product_price, quantity, total_price, receiver_name, receiver_phone,
               receiver_address, status, create_time, update_time
        FROM orders
        WHERE user_id = #{userId}
        ORDER BY create_time DESC
    </select>

    <select id="findById" resultType="com.ecommerce.entity.Order">
        SELECT id, order_no, user_id, product_id, product_name, product_image,
               product_price, quantity, total_price, receiver_name, receiver_phone,
               receiver_address, status, create_time, update_time
        FROM orders
        WHERE id = #{id}
    </select>

    <select id="findAll" resultType="com.ecommerce.entity.Order">
        SELECT id, order_no, user_id, product_id, product_name, product_image,
               product_price, quantity, total_price, receiver_name, receiver_phone,
               receiver_address, status, create_time, update_time
        FROM orders
        ORDER BY create_time DESC
        LIMIT #{offset}, #{size}
    </select>

    <select id="countAll" resultType="int">
        SELECT COUNT(*) FROM orders
    </select>

    <update id="updateStatus">
        UPDATE orders SET status = #{status} WHERE id = #{id}
    </update>

</mapper>
```

**说明：**
- 表名为 `orders`（不是 `t_order`，不是 `order`，因为order是MySQL关键字）
- INSERT和SELECT中包含 `receiver_name`、`receiver_phone`、`receiver_address` 字段，与Order实体类的 `receiverName`、`receiverPhone`、`receiverAddress` 对应
- 包含 `update_time` 字段

---

### 步骤10：验证数据库连接

重启SpringBoot项目，观察控制台输出。

**预期效果：** 项目正常启动，没有报错。控制台没有SQL错误（因为还没有调用Mapper方法，所以不会有SQL输出）。

如果想验证Mapper是否正常，可以临时在TestController中注入UserMapper进行测试。但更规范的验证将在第7章通过完整的登录接口来实现。

---

## 本章常见问题

| 问题 | 原因 | 解决方案 |
|------|------|---------|
| 启动报错"Communications link failure" | MySQL未启动或连接配置错误 | 检查MySQL服务状态，确认application.yml中url/username/password正确 |
| 启动报错"Invalid bound statement" | Mapper XML的namespace与接口不匹配 | 确保XML中namespace等于Mapper接口的完整类名 |
| Mapper注入报红 | 没有添加@MapperScan | 在启动类上添加@MapperScan("com.ecommerce.mapper") |
| 访问/api/test返回404 | Controller包路径不在扫描范围内 | 确保Controller在com.ecommerce包或其子包下 |
| 访问/api/test前端报跨域错误 | 跨域配置未生效 | 检查WebMvcConfig是否有@Configuration注解 |
| Lombok不生效 | 没有安装Lombok插件 | IDEA → Settings → Plugins → 搜索Lombok → Install |
| javax.servlet报红 | SpringBoot 3.x使用jakarta命名空间 | 所有javax.servlet替换为jakarta.servlet |
| pom.xml依赖下载失败 | Maven仓库连接慢 | 配置国内Maven镜像（阿里云） |

---

## 本章小结

- ✅ 创建了SpringBoot 3.5.3项目（JDK 17），配置了Maven依赖
- ✅ 配置了application.yml（数据源、MyBatis、AI预留配置）
- ✅ 编写了统一响应类Result<T>（放在util包下，使用Lombok @Data）
- ✅ 编写了全局异常处理器GlobalExceptionHandler
- ✅ 配置了WebMvcConfig跨域，允许前端访问
- ✅ 编写了3个实体类（User、Product、Order），与数据库表一一对应
- ✅ 编写了3个Mapper接口（@Mapper注解）和XML映射文件（表名user/product/orders）
- ✅ 验证了项目能正常启动并返回JSON

**下一章预告：** 第7章将实现用户登录/退出/注册接口，引入JWT认证机制。
