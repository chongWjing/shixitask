# 第13章：Spring AI 集成

## 本章目标

> "同学们，我们的花之恋鲜花电商平台已经有了完整的CRUD功能，但是缺少一些'智能'的东西。比如用户搜索鲜花时，只能精确匹配关键词；管理员想查数据时，必须自己写条件组合。这章我们要引入Spring AI，给项目装上'大脑'——让用户能用全文搜索更智能地搜鲜花、让管理员能用自然语言查数据！"

完成本章后，你将能够：
- 在SpringBoot项目中集成Spring AI并调用DeepSeek大模型
- 实现基于MySQL全文索引的智能搜索功能
- 实现自然语言转SQL的智能表格查询（含严格安全检查）
- 实现AI对话接口和AiQuery.vue前端页面
- 了解MockChatModel（没有API Key也能理解原理）
- 了解根据图片生成页面的AI工具

---

## 13.1 Spring AI 配置

> "Spring AI是Spring官方推出的AI集成框架，它让我们在SpringBoot项目中调用大模型变得像调用数据库一样简单。我们不需要去学各种AI SDK的API，只需要用Spring的风格就能搞定。"

### 13.1.1 Spring AI 核心概念

| 概念 | 说明 | 类比 |
|------|------|------|
| **ChatModel** | AI模型的抽象，如DeepSeek、OpenAI | 类似DataSource |
| **Prompt** | 发送给AI的提示词 | 类似SQL语句 |
| **ChatResponse** | AI的响应结果 | 类似查询结果集 |
| **JdbcTemplate** | 执行SQL的工具 | 直接执行AI生成的SQL |

### 13.1.2 添加Spring AI依赖

#### 步骤1：修改pom.xml

在 `<dependencies>` 标签内添加：

```xml
<!-- Spring AI OpenAI Starter -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
    <version>1.0.0-M4</version>
</dependency>
```

在 `</dependencies>` 后面添加Spring AI的里程碑仓库（Spring AI的里程碑版本不在Maven中央仓库，必须添加才能下载）：

```xml
<repositories>
    <repository>
        <id>spring-milestones</id>
        <name>Spring Milestones</name>
        <url>https://repo.spring.io/milestone</url>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
    </repository>
    <repository>
        <id>spring-snapshots</id>
        <name>Spring Snapshots</name>
        <url>https://repo.spring.io/snapshot</url>
        <releases>
            <enabled>false</enabled>
        </releases>
    </repository>
</repositories>
```

> "M4表示Milestone 4，是里程碑版本，还没正式发布到Maven中央仓库，所以必须配置Spring的里程碑仓库。"

#### 步骤2：在application.yml中配置Spring AI

打开 `backend/src/main/resources/application.yml`，在spring节点下添加AI配置：

```yaml
spring:
  ai:
    openai:
      api-key: sk-your-deepseek-api-key    # 替换为你的DeepSeek API Key
      base-url: https://api.deepseek.com   # DeepSeek的OpenAI兼容接口
      chat:
        options:
          model: deepseek-chat
          temperature: 0.7
```

> "几个关键点：`base-url`要改成DeepSeek的地址，因为Spring AI默认连OpenAI，而DeepSeek提供了OpenAI兼容接口；`model`填`deepseek-chat`；`temperature`控制AI回答的随机性，0.7是比较平衡的值。"

> "获取DeepSeek API Key：访问 https://platform.deepseek.com/ ，注册账号后在API Keys页面创建即可，新用户通常有免费额度。如果你暂时没有API Key，可以先不配，后面13.5节有Mock方案。"

#### 步骤3：刷新Maven依赖

在IDEA中点击Maven面板的刷新按钮，或执行：

```bash
mvn clean install -DskipTests
```

**预期效果：** 控制台显示 `BUILD SUCCESS`，所有依赖下载成功。

---

## 13.2 智能全文搜索

> "目前我们的搜索是用LIKE模糊匹配，搜'玫瑰'只能匹配包含'玫瑰'字样的商品。全文搜索基于分词，搜'红玫瑰'也能匹配到'玫瑰'，更智能也更高效。"

### 13.2.1 全文搜索 vs 模糊查询

| 对比项 | LIKE模糊查询 | FULLTEXT全文搜索 |
|--------|-------------|-----------------|
| 搜索方式 | 逐字符匹配 | 基于词的分词匹配 |
| 性能 | 全表扫描，数据量大时慢 | 使用索引，速度快 |
| 相关度排序 | 不支持 | 支持，按相关度排序 |
| 中文支持 | 原生支持 | 需要ngram解析器 |

### 13.2.2 给product表添加FULLTEXT索引

打开MySQL客户端，连接到 `ecommerce` 数据库，执行：

```sql
-- 给product表的name和description字段添加全文索引
-- WITH PARSER ngram 表示使用ngram分词器，支持中文
ALTER TABLE product ADD FULLTEXT INDEX ft_name_desc (name, description) WITH PARSER ngram;
```

> "`ngram`是MySQL内置的中文分词解析器，它会把中文文本按2个字符一组切分。比如'红玫瑰'会被切成'红玫'和'玫瑰'，这样搜'玫瑰'就能匹配到了。"

验证索引是否创建成功：

```sql
SHOW INDEX FROM product WHERE Index_type = 'FULLTEXT';
```

### 13.2.3 编写搜索接口

#### 步骤1：修改ProductMapper添加搜索方法

打开 `backend/src/main/java/com/ecommerce/mapper/ProductMapper.java`，添加全文搜索方法：

```java
package com.ecommerce.mapper;

import com.ecommerce.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductMapper {
    List<Product> findAll();
    List<Product> findByPage(int offset, int size);
    Product findById(Integer id);
    long count();
    List<Product> searchByKeyword(String keyword);
    int decreaseStock(@Param("id") Integer id, @Param("quantity") Integer quantity);
    int increaseSales(@Param("id") Integer id, @Param("quantity") Integer quantity);
    int insert(Product product);
    int update(Product product);
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
    List<Product> findAllByPage(@Param("offset") int offset, @Param("size") int size);
    long countAll();
    List<Product> findByCategoryPage(@Param("offset") int offset, @Param("size") int size, @Param("category") String category);
    long countByCategory(@Param("category") String category);

    /**
     * 全文搜索鲜花商品
     * 使用MySQL FULLTEXT索引的MATCH AGAINST语法
     */
    List<Product> searchByKeywordFulltext(@Param("keyword") String keyword);
}
```

> "ProductMapper 包含多个方法：`findAll`/`findByPage` 用于前台鲜花列表，`findAllByPage`/`countAll` 用于后台管理（不过滤status），`findById` 用于鲜花详情，`searchByKeyword` 是LIKE模糊搜索，`searchByKeywordFulltext` 是全文搜索（第13章新增），`findByCategoryPage`/`countByCategory` 用于分类筛选（鲜花/永生花/花束/花篮/绿植/礼品花），`decreaseStock`/`increaseSales` 用于下单扣库存加销量，`insert`/`update`/`updateStatus` 用于后台鲜花管理。全文搜索方法 `searchByKeywordFulltext` 对应的SQL定义在ProductMapper.xml中，使用 `MATCH ... AGAINST ... IN NATURAL LANGUAGE MODE` 语法，按相关度排序。"

#### 步骤2：修改ProductService添加搜索方法

打开 `backend/src/main/java/com/ecommerce/service/ProductService.java`，添加全文搜索方法：

```java
package com.ecommerce.service;

import com.ecommerce.entity.PageResult;
import com.ecommerce.entity.Product;
import com.ecommerce.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    public PageResult<Product> getProductList(int page, int size) {
        int offset = (page - 1) * size;
        List<Product> products = productMapper.findByPage(offset, size);
        long total = productMapper.count();
        return new PageResult<>(products, total, page, size);
    }

    public Product getProductById(Integer id) {
        return productMapper.findById(id);
    }

    public List<Product> searchProduct(String keyword) {
        return productMapper.searchByKeyword(keyword);
    }

    public PageResult<Product> getProductListByCategory(int page, int size, String category) {
        int offset = (page - 1) * size;
        List<Product> products = productMapper.findByCategoryPage(offset, size, category);
        long total = productMapper.countByCategory(category);
        return new PageResult<>(products, total, page, size);
    }

    /**
     * 全文搜索鲜花商品（优先全文索引，无结果时回退到LIKE模糊查询）
     */
    public List<Product> searchProductFulltext(String keyword) {
        List<Product> results = productMapper.searchByKeywordFulltext(keyword);
        if (results.isEmpty()) {
            // fallback到LIKE模糊查询
            results = productMapper.searchByKeyword(keyword);
        }
        return results;
    }

    public String addProduct(Product product) {
        productMapper.insert(product);
        return "添加成功";
    }

    public String updateProduct(Product product) {
        productMapper.update(product);
        return "更新成功";
    }

    public String updateProductStatus(Integer id, Integer status) {
        productMapper.updateStatus(id, status);
        return "状态更新成功";
    }
}
```

> "ProductService中 `searchProductFulltext` 方法优先使用全文搜索 `searchByKeywordFulltext`，如果全文搜索没有结果则回退到LIKE模糊查询 `searchByKeyword`，这样既保证了搜索的智能性，也保证了搜索的覆盖率。"

#### 步骤3：修改ProductController添加搜索接口

打开 `backend/src/main/java/com/ecommerce/controller/ProductController.java`，添加搜索接口（升级第8章的LIKE搜索）：

```java
package com.ecommerce.controller;

import com.ecommerce.entity.PageResult;
import com.ecommerce.entity.Product;
import com.ecommerce.service.ProductService;
import com.ecommerce.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String category) {
        PageResult<Product> pageResult;
        if (category != null && !category.isEmpty()) {
            pageResult = productService.getProductListByCategory(page, size, category);
        } else {
            pageResult = productService.getProductList(page, size);
        }
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable Integer id) {
        Product product = productService.getProductById(id);
        return Result.success(product);
    }

    /**
     * 全文搜索鲜花商品（升级版搜索）
     * GET /api/product/search?keyword=玫瑰
     */
    @GetMapping("/search")
    public Result search(@RequestParam String keyword) {
        List<Product> list = productService.searchProduct(keyword);
        return Result.success(list);
    }
}
```

**预期效果：** 启动项目后，访问 `http://localhost:8080/api/product/search?keyword=玫瑰` 能返回匹配的鲜花商品列表。

---

## 13.3 智能表格（AI查询）

> "现在来做一个更酷的功能——智能表格！管理员用中文提问，比如'今天有多少鲜花订单''哪种花束卖得最好'，系统自动把自然语言转成SQL，执行查询，返回结果。"

### 13.3.1 原理流程

```
用户输入："今天有多少鲜花订单？"
    ↓
构造Prompt（包含数据库表结构 + 用户问题）
    ↓
调用DeepSeek AI
    ↓
AI返回SQL："SELECT COUNT(*) FROM orders WHERE DATE(create_time) = CURDATE()"
    ↓
SQL安全检查（只允许SELECT，禁止DROP/DELETE/INSERT/UPDATE/ALTER/CREATE/TRUNCATE）
    ↓
用JdbcTemplate执行SQL
    ↓
返回查询结果
```

### 13.3.2 编写AiService

> "AI查询的核心逻辑放在AiService中，遵循Controller-Service分层原则。AiService负责：调用AI生成SQL、SQL安全校验、执行SQL。AiController只负责接收请求和权限校验。"

创建文件 `backend/src/main/java/com/ecommerce/service/AiService.java`：

```java
package com.ecommerce.service;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AiService {

    @Autowired
    private OpenAiChatModel chatModel;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SYSTEM_PROMPT = """
        你是一个SQL专家。根据用户的问题，生成对应的MySQL SQL查询语句。
        数据库表结构如下：
        - product(id, name, price, original_price, description, category, image, stock, sales, status, create_time)  -- 鲜花商品表，category包括：鲜花/永生花/花束/花篮/绿植/礼品花
        - orders(id, order_no, user_id, product_id, product_name, product_image, product_price, quantity, total_price, receiver_name, receiver_phone, receiver_address, status, create_time, update_time)
        - user(id, username, phone, role, status, create_time)

        规则：
        1. 只生成SELECT语句，不要生成INSERT/UPDATE/DELETE/DROP等语句
        2. 查询结果限制最多50行，使用LIMIT
        3. 只返回纯SQL，不要包含任何解释或markdown格式
        4. 字段名使用下划线命名（如product_name）
        5. 不要查询user表的password字段
        """;

    // 最大返回行数
    private static final int MAX_ROWS = 50;

    @Transactional(readOnly = true)
    public Map<String, Object> query(String question) {
        // 1. 调用AI生成SQL
        String sql = generateSql(question);

        // 2. 校验SQL安全性
        validateSql(sql);

        // 3. 执行SQL
        List<Map<String, Object>> results = executeQuery(sql);

        // 4. 限制结果行数
        if (results.size() > MAX_ROWS) {
            results = results.subList(0, MAX_ROWS);
        }

        // 5. 返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("question", question);
        data.put("sql", sql);
        data.put("results", results);
        return data;
    }

    private String generateSql(String question) {
        Prompt prompt = new Prompt(List.of(
            new SystemMessage(SYSTEM_PROMPT),
            new UserMessage(question)
        ));
        ChatResponse response = chatModel.call(prompt);
        String sql = response.getResult().getOutput().getContent().trim();
        // 去除可能的markdown代码块标记
        sql = sql.replaceAll("```sql\\s*", "").replaceAll("```\\s*", "").trim();
        return sql;
    }

    /**
     * 增强的SQL安全校验
     * 防止SQL注入、多语句执行、UNION注入、文件操作等攻击
     */
    private void validateSql(String sql) {
        String upperSql = sql.toUpperCase().trim();

        // 1. 必须以SELECT开头
        if (!upperSql.startsWith("SELECT")) {
            throw new RuntimeException("仅允许查询操作，SQL必须以SELECT开头");
        }

        // 2. 禁止分号（防止多语句执行）
        if (sql.contains(";")) {
            throw new RuntimeException("SQL中不允许包含分号");
        }

        // 3. 禁止注释符号（防止注释注入）
        if (sql.contains("--") || sql.contains("/*") || sql.contains("*/")) {
            throw new RuntimeException("SQL中不允许包含注释符号");
        }

        // 4. 禁止危险关键字
        String[] dangerousKeywords = {
            "UNION", "INTO OUTFILE", "INTO DUMPFILE", "LOAD_FILE",
            "INFORMATION_SCHEMA", "LOAD DATA",
            "DROP ", "DELETE ", "UPDATE ", "INSERT ", "ALTER ",
            "CREATE ", "TRUNCATE", "EXEC ", "EXECUTE ",
            "GRANT ", "REVOKE ", "CALL "
        };
        for (String keyword : dangerousKeywords) {
            if (upperSql.contains(keyword)) {
                throw new RuntimeException("SQL中包含不允许的关键字: " + keyword.trim());
            }
        }

        // 5. 禁止查询user表的password字段
        if (upperSql.contains("PASSWORD") && upperSql.contains("USER")) {
            throw new RuntimeException("不允许查询用户密码信息");
        }
    }

    private List<Map<String, Object>> executeQuery(String sql) {
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            throw new RuntimeException("SQL执行失败：" + e.getMessage());
        }
    }
}
```

> "AiService的核心是 `query()` 方法，它按顺序执行5个步骤：1)调用AI生成SQL，2)校验SQL安全性，3)执行SQL，4)限制结果行数，5)返回结果。`validateSql()` 是增强版的SQL安全校验，包含5层检查：必须以SELECT开头、禁止分号（防止多语句执行）、禁止注释符号（防止注释注入）、禁止UNION/INTO OUTFILE/LOAD_FILE等危险关键字、禁止查询user表password字段。"

### 13.3.3 编写AiController

创建文件 `backend/src/main/java/com/ecommerce/controller/AiController.java`：

```java
package com.ecommerce.controller;

import com.ecommerce.service.AiService;
import com.ecommerce.service.UserService;
import com.ecommerce.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @Autowired
    private UserService userService;

    @PostMapping("/query")
    public Result query(HttpServletRequest request, @RequestBody Map<String, String> params) {
        // 校验管理员权限
        Integer userId = (Integer) request.getAttribute("userId");
        if (!userService.checkAdmin(userId)) {
            return Result.error(403, "无权限");
        }

        String question = params.get("question");
        if (question == null || question.trim().isEmpty()) {
            return Result.error("请输入查询问题");
        }
        Map<String, Object> result = aiService.query(question);
        return Result.success(result);
    }
}
```

> "AiController 遵循Controller-Service分层：Controller只负责接收请求、权限校验（checkAdmin）和参数校验，核心的AI调用、SQL生成、SQL安全校验、SQL执行都放在AiService中。AiController注入AiService和UserService，不直接注入ChatModel或JdbcTemplate。智能查询接口需要管理员权限，普通用户无法使用。"

**预期效果：** 发送POST请求到 `/api/ai/query` 能正常返回查询结果。

测试1 - 查询订单数量：

```bash
curl -X POST http://localhost:8080/api/ai/query \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-token>" \
  -d "{\"question\": \"总共有多少订单\"}"
```

预期返回：

```json
{
    "code": 200,
    "data": {
        "question": "总共有多少订单",
        "sql": "SELECT COUNT(*) AS total_orders FROM orders",
        "results": [{"total_orders": 25}]
    }
}
```

测试2 - 查询最畅销鲜花：

```bash
curl -X POST http://localhost:8080/api/ai/query \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-token>" \
  -d "{\"question\": \"哪种鲜花卖得最好\"}"
```

预期返回：

```json
{
    "code": 200,
    "data": {
        "question": "哪种鲜花卖得最好",
        "sql": "SELECT p.name, p.sales FROM product p ORDER BY p.sales DESC LIMIT 1",
        "results": [{"name": "红玫瑰花束", "sales": 5678}]
    }
}
```

---

## 13.4 AI前端页面（AiQuery.vue）

> "后端AI接口已经就绪，现在需要一个管理后台页面让管理员使用AI查询功能。我们用ElementPlus创建AiQuery.vue组件。"

### 步骤1：创建AiQuery.vue组件

创建文件 `frontend/src/views/admin/AiQuery.vue`：

```vue
<!-- AI智能查询页 - 自然语言查询/快捷提问/SQL展示/结果表格 -->
<template>
  <div class="ai-query-page">
    <el-card shadow="never">
      <template #header>
        <span>AI 智能查询</span>
      </template>

      <!-- 查询输入区域 -->
      <el-row :gutter="12" align="middle">
        <el-col :span="18">
          <el-input
            v-model="question"
            placeholder="请输入您的问题，例如：今天卖了多少束花？"
            size="large"
            @keyup.enter="onQuery"
            clearable
          />
        </el-col>
        <el-col :span="6">
          <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="onQuery">
            查询
          </el-button>
        </el-col>
      </el-row>

      <!-- 快捷提问按钮 -->
      <div class="quick-questions">
        <span class="quick-label">快捷提问：</span>
        <el-button
          v-for="q in quickQuestions"
          :key="q"
          size="small"
          round
          @click="question = q; onQuery()"
        >
          {{ q }}
        </el-button>
      </div>

      <!-- 查询结果展示 -->
      <div v-if="result" class="result-section">
        <!-- SQL代码展示区 -->
        <div v-if="result.sql" class="sql-section">
          <div class="section-title">生成的 SQL</div>
          <div class="sql-code">
            <code>{{ result.sql }}</code>
          </div>
        </div>

        <!-- 结果表格（动态列） -->
        <div v-if="result.columns && result.columns.length && result.data && result.data.length" class="table-section">
          <div class="section-title">查询结果（共 {{ result.data.length }} 条）</div>
          <el-table :data="result.data" stripe border style="width: 100%" max-height="400">
            <el-table-column
              v-for="col in result.columns"
              :key="col"
              :prop="col"
              :label="col"
              min-width="120"
            />
          </el-table>
        </div>

        <!-- 文本回答 -->
        <div v-if="result.answer" class="answer-section">
          <div class="section-title">回答</div>
          <div class="answer-text">{{ result.answer }}</div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-else-if="!loading" description="请输入问题开始查询" />
    </el-card>
  </div>
</template>

<script>
import { aiQuery } from '../../api/admin'

export default {
  name: 'AdminAiQuery',
  data() {
    return {
      /* 用户输入的问题 */
      question: '',
      /* 查询结果 */
      result: null,
      loading: false,
      /* 预设快捷问题 */
      quickQuestions: [
        '今天卖了多少束花？',
        '销量最高的鲜花是什么？',
        '本周的销售额是多少？',
        '哪个鲜花分类最受欢迎？',
        '最近7天的订单趋势如何？'
      ]
    }
  },
  methods: {
    /** 发起AI查询 */
    async onQuery() {
      if (!this.question.trim()) {
        this.$message.warning('请输入问题')
        return
      }
      this.loading = true
      this.result = null
      try {
        const res = await aiQuery({ question: this.question })
        this.result = res.data || {}
      } catch (e) {
        // 错误由拦截器处理
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.ai-query-page {
  padding: 20px;
}

/* 快捷提问区域 */
.quick-questions {
  margin-top: 16px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.quick-label {
  color: #999;
  font-size: 14px;
  white-space: nowrap;
}

/* 结果区域 */
.result-section {
  margin-top: 24px;
}

.section-title {
  font-size: 15px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

/* SQL代码展示区 - 深色背景等宽字体 */
.sql-section {
  margin-bottom: 20px;
}

.sql-code {
  background: #1e1e1e;
  color: #d4d4d4;
  padding: 16px;
  border-radius: 6px;
  overflow-x: auto;
}

.sql-code code {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
}

/* 表格区域 */
.table-section {
  margin-bottom: 20px;
}

/* 文本回答区域 */
.answer-section {
  margin-bottom: 20px;
}

.answer-text {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 6px;
  color: #333;
  line-height: 1.8;
  font-size: 14px;
}
</style>
```

> "对比之前的 ai-query.html（Bootstrap HTML + 原生 fetch），AiQuery.vue 使用 Element Plus 组件（`el-input`、`el-card`、`el-table`、`el-button`、`el-empty`）构建界面，通过 `import { aiQuery } from '../../api/admin'` 统一调用接口，而不是直接用 fetch。注意实际代码只导入 `aiQuery`，不需要 `aiChat`（当前版本聚焦于智能查询功能）。访问地址为 `http://localhost:5174/admin/ai`。快捷问题已针对花之恋鲜花电商场景定制，如'今天卖了多少束花'、'哪个鲜花分类最受欢迎'等。"

**预期效果：**
- 在后台主页点击"智能查询"，路由切换到此页面
- 输入自然语言问题，AI自动生成SQL并执行查询
- 展示生成的SQL代码（深色背景等宽字体）和查询结果表格（动态列名）
- 快捷问题按钮，点击直接查询
- 使用 Options API 风格

---

## 13.5 AI查询接口测试

> "实际代码中AiController只提供 `/api/ai/query` 智能查询接口（需管理员权限），没有独立的 `/api/ai/chat` 对话接口。AI对话功能集成在智能查询流程中。"

### 13.5.1 测试智能查询接口

启动项目后，发送POST请求测试（需要管理员token）：

```bash
curl -X POST http://localhost:8080/api/ai/query \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-admin-token>" \
  -d "{\"question\": \"今天有多少鲜花订单\"}"
```

预期返回：

```json
{
    "code": 200,
    "data": {
        "question": "今天有多少鲜花订单",
        "sql": "SELECT COUNT(*) AS total_orders FROM orders WHERE DATE(create_time) = CURDATE()",
        "results": [{"total_orders": 7}]
    }
}
```

> "如果看到了AI生成的SQL和查询结果，恭喜你，Spring AI集成成功了！如果报错，检查一下API Key是否正确、网络是否能访问DeepSeek的接口、请求头是否带了管理员token。"

---

## 13.6 MockChatModel（没有API Key时使用）

> "有些同学可能暂时没有API Key，没关系，我们可以先用Mock方式来理解原理。Mock方式就是用一个假的服务来模拟AI的返回，这样先把整个流程走通，等有了Key再替换成真实的。"

### 13.6.1 创建MockChatModel

创建文件 `backend/src/main/java/com/ecommerce/config/MockChatModel.java`：

```java
package com.ecommerce.config;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.Collections;

/**
 * Mock聊天模型 - 用于没有API Key时测试
 * 实现ChatModel接口，返回模拟的AI回复
 */
public class MockChatModel implements ChatModel {

    @Override
    public ChatResponse call(Prompt prompt) {
        // 获取用户消息
        var userMessage = prompt.getContents();
        var mockResponse = "【Mock模式】你问了：" + userMessage
                + "。这是一个模拟的AI回复。请配置真实的DeepSeek API Key以获得实际AI响应。";

        var assistantMessage = new AssistantMessage(mockResponse);
        var generation = new Generation(assistantMessage);
        return new ChatResponse(Collections.singletonList(generation));
    }
}
```

### 13.6.2 创建Mock配置类

创建文件 `backend/src/main/java/com/ecommerce/config/AiMockConfig.java`：

```java
package com.ecommerce.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Mock配置 - 在没有API Key时使用
 * 激活方式：在application.yml中设置 spring.profiles.active=mock
 * 或启动参数：--spring.profiles.active=mock
 */
@Configuration
@Profile("mock")
public class AiMockConfig {

    @Bean
    public ChatModel chatModel() {
        return new MockChatModel();
    }
}
```

### 13.6.3 创建Mock模式的配置文件

创建文件 `backend/src/main/resources/application-mock.yml`：

```yaml
# Mock模式配置 - 在没有API Key时激活
# 启动时使用：--spring.profiles.active=mock
spring:
  ai:
    openai:
      api-key: sk-mock
      base-url: https://mock.local
```

### 13.6.4 使用Mock模式启动

启动项目时加上Mock参数：

```bash
java -jar target/ecommerce-0.0.1-SNAPSHOT.jar --spring.profiles.active=mock
```

或在 `application.yml` 中设置：

```yaml
spring:
  profiles:
    active: mock
```

**预期效果：** 使用Mock模式启动后，访问 `/api/ai/query` 不会报错，返回模拟的回复。

> "Mock模式的核心思想：用MockChatModel替换真实的ChatModel Bean。当`@Profile("mock")`激活时，Spring会使用MockChatModel，而不是Spring AI自动配置的真实ChatModel。有了API Key后，去掉mock profile即可切换回真实模式。"

---

## 13.7 图片生代码

> "这是一个非常前沿的话题——根据设计图片直接生成代码！虽然我们项目不会直接用，但了解这些工具对以后的工作非常有帮助。这一节只介绍原理和工具使用流程，不需要写代码。"

### 13.7.1 原理

AI大模型（如GPT-4V、Claude 3等）具备"看图理解"的能力，它能看到设计图上的布局、颜色、文字，然后把这些视觉信息转化成HTML/CSS/Vue代码。

```
设计图（Figma/Sketch/截图）
    ↓
上传到AI工具
    ↓
AI识别：布局结构、颜色方案、字体大小、组件类型
    ↓
生成：HTML + CSS + JavaScript代码
    ↓
开发者微调 → 完成！
```

### 13.7.2 工具介绍

#### 工具1：Trae AI

Trae AI是AI编程编辑器，支持根据设计图生成代码。

使用步骤：
1. 打开Trae AI编辑器
2. 在Chat对话框中上传设计图片
3. 输入提示词："请根据这张设计图生成Vue3组件代码，使用ElementPlus组件库"
4. AI会生成完整的Vue组件代码
5. 将代码复制到项目中，根据实际情况微调

#### 工具2：v0.dev（Vercel AI UI生成）

v0.dev是Vercel推出的AI UI生成工具，专门用来生成React/Vue组件。

使用步骤：
1. 访问 https://v0.dev
2. 上传设计图或描述UI需求
3. AI生成组件代码和预览效果
4. 复制代码到项目中使用

#### 工具3：Screenshot-to-Code

开源项目，专门把截图转成代码，调用GPT-4V来识别图片内容。

使用步骤：
1. 访问GitHub上的screenshot-to-code项目
2. 上传设计截图
3. AI生成HTML/Tailwind CSS代码
4. 可以实时预览和调整

### 13.7.3 实际示例：给AI的Prompt

假设要生成鲜花详情页，Prompt可以这样写：

```
请根据设计图生成一个Vue3鲜花详情页组件，要求：
1. 使用ElementPlus组件库
2. 顶部是鲜花轮播图
3. 鲜花名称、价格展示
4. 底部固定购买按钮栏
5. 样式要和设计图尽量一致
6. 组件名为ProductDetail
7. props接收productId
8. 用axios从/api/product/{id}获取鲜花数据
```

### 13.7.4 应用场景

| 场景 | 说明 | 推荐工具 |
|------|------|---------|
| 快速原型 | 根据设计图快速生成页面骨架 | v0.dev、Trae AI |
| 组件开发 | 根据设计稿生成单个组件 | Screenshot-to-Code |
| 样式还原 | 将视觉设计转成CSS代码 | Trae AI、Copilot |
| 学习参考 | 看到好的设计，生成代码学习 | ChatGPT、Claude |

> "注意，AI生成的代码通常需要人工调整才能完美还原设计。AI更多是帮我们快速搭建框架，细节调整还需要开发者来做。"

---

## 常见问题

| 序号 | 问题 | 原因 | 解决方案 |
|------|------|------|---------|
| 1 | Maven下载Spring AI依赖失败 | 没有配置Spring里程碑仓库 | 在pom.xml中添加spring-milestones仓库配置 |
| 2 | 启动报错"Invalid API Key" | DeepSeek API Key配置错误或未配置 | 检查application.yml中的api-key是否正确，或使用Mock模式 |
| 3 | 调用AI接口超时 | 网络问题或DeepSeek服务响应慢 | 检查网络连接，可增加超时配置 |
| 4 | FULLTEXT索引创建失败 | MySQL版本不支持ngram或表已有全文索引 | 确认MySQL版本>=5.6，先执行`SHOW INDEX FROM product`检查是否已存在 |
| 5 | 全文搜索搜不到结果 | ngram分词最小长度限制 | MySQL的ngram_token_size默认为2，单字搜索可能搜不到 |
| 6 | 智能查询返回安全限制 | AI生成了非SELECT语句 | 换一种方式提问，或在Prompt中强调只能生成SELECT |
| 7 | 智能查询报SQL语法错误 | AI生成的SQL有误 | 查看返回的SQL语句，手动在MySQL中测试，调整Prompt |
| 8 | JdbcTemplate注入失败 | 项目未引入jdbc依赖 | 确认pom.xml中有mybatis-spring-boot-starter，它包含JdbcTemplate |
| 9 | MockChatModel编译报错 | Spring AI版本差异导致接口方法不同 | 确认使用spring-ai-openai-spring-boot-starter 1.0.0-M4版本 |
| 10 | AI返回的SQL带markdown格式 | AI习惯用代码块包裹SQL | generateSql方法中用replaceAll清理markdown标记 |
| 11 | AiQuery页面空白 | 路由未配置 | 检查router/index.js中是否有/admin/ai路由和AiQuery组件 |
| 12 | 智能查询返回403 | 未携带管理员token | AiController需要管理员权限，确保请求头带有效token |
| 13 | 智能查询报"仅允许查询操作" | AI生成了非SELECT语句 | 换一种方式提问，或在Prompt中强调只能生成SELECT |

---

## 本章小结

> "同学们，这章我们给花之恋鲜花电商平台装上了'AI大脑'！让我们回顾一下学到了什么。"

1. **Spring AI集成**：学会了在SpringBoot 3.x项目中引入Spring AI 1.0.0-M4，配置DeepSeek大模型，通过OpenAiChatModel调用AI接口。使用JDK 17的text block语法编写系统提示词。

2. **智能全文搜索**：学会了使用MySQL FULLTEXT索引实现全文搜索，关键是添加 `WITH PARSER ngram` 支持中文分词，使用 `MATCH ... AGAINST ... IN NATURAL LANGUAGE MODE` 语法查询。ProductMapper的 `searchByKeywordFulltext` 方法比LIKE更智能，ProductService的 `searchProductFulltext` 方法在全文搜索无结果时回退到LIKE模糊查询。

3. **智能表格**：学会了用Spring AI实现自然语言转SQL查询。遵循Controller-Service分层：AiController负责权限校验和参数校验，AiService负责调用AI生成SQL、SQL安全校验、执行SQL。`validateSql()` 增强版安全校验包含5层检查：必须以SELECT开头、禁止分号（防止多语句执行）、禁止注释符号（防止注释注入）、禁止UNION/INTO OUTFILE/LOAD_FILE等危险关键字、禁止查询user表password字段。

4. **AiQuery.vue前端页面**：使用ElementPlus组件构建AI查询页面（Options API风格），通过 `import { aiQuery } from '../../api/admin'` 统一调用接口，访问地址为 `http://localhost:5174/admin/ai`。SQL展示区使用深色背景等宽字体，查询结果使用动态列el-table。

5. **AiController权限控制**：智能查询接口 `/api/ai/query` 需要管理员权限，通过 `userService.checkAdmin(userId)` 校验，普通用户无法使用。

6. **MockChatModel**：提供了没有API Key时的Mock方案，通过Spring Profile机制切换，确保MockChatModel编译无错误。

7. **图片生代码**：了解了Trae AI、v0.dev、Screenshot-to-Code等工具的原理和使用流程。
