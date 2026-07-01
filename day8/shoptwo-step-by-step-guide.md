# ShopTwo 递增式开发指南

> 基于 shoptwo 项目，实现首页与商品详情页功能模块  
> 每完成一步即可预览验证，逐步叠加功能 | 总时长约 3 小时（180 分钟）

## 目录

- [零、项目初始状态确认](#零项目初始状态确认) `5min`
- [一、首页结构分析与 Layout 提取](#一首页结构分析与-layout-提取) `35min`
- [二、首页后端完善](#二首页后端完善) `40min`
- [三、商品详情页](#三商品详情页) `35min`
- [四、评价列表页](#四评价列表页) `20min`
- [五、路由完善与登录优化](#五路由完善与登录优化) `25min`
- [六、项目部署与演示](#六项目部署与演示) `20min`

###零、项目初始状态确认

```
backend/src/main/java/com/huashan/backend/
├── entity/
│   ├── Product.java          # 商品实体
│   └── Category.java         # 分类实体
├── mapper/
│   ├── ProductMapper.java    # 商品 Mapper
│   └── CategoryMapper.java   # 分类 Mapper
├── service/
│   ├── ProductService.java
│   ├── ProductServiceImpl.java
│   ├── CategoryService.java
│   └── CategoryServiceImpl.java
├── controller/
│   ├── ProductController.java  # 已有 list() 方法
│   └── CategoryController.java # 已有 list() 方法
└── util/
    ├── Result.java
    └── PageResult.java

frontend/src/api/
└── product.js   # 初始只有 getProductList + getCategoryList
```

#### ProductMapper.java（已有）

```java
package com.huashan.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huashan.backend.entity.Product;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
 /**
     * 分页查询所有上架商品
     * @param offset 偏移量（起始位置）
     * @param size 每页条数
     * @return 商品列表
     */
    List<Product> findByPage(int offset, int size);

    /**
     * 统计所有上架商品的总数
     * @return 商品总数
     */
    long count();

    /**
     * 根据关键词搜索商品（不分页）
     * @param keyword 搜索关键词
     * @return 匹配的商品列表
     */
    List<Product> searchByKeyword(String keyword);

    /**
     * 按分类分页查询商品
     * @param offset 偏移量
     * @param size 每页条数
     * @param category 分类名称
     * @return 该分类下的商品列表
     */
    List<Product> findByCategoryPage(@Param("offset") int offset, @Param("size") int size, @Param("category") String category);

    /**
     * 统计指定分类下的商品总数
     * @param category 分类名称
     * @return 商品总数
     */
    long countByCategory(@Param("category") String category);

    /**
     * 全文搜索商品（MySQL FULLTEXT + ngram）
     * @param keyword 搜索关键词
     * @return 匹配的商品列表（按相关度排序）
     */
    List<Product> searchByKeywordFulltext(@Param("keyword") String keyword);

    /**
     * 根据关键词分页搜索商品
     * @param keyword 搜索关键词
     * @param offset 偏移量
     * @param size 每页条数
     * @return 匹配的商品列表
     */
    List<Product> searchByKeywordPage(@Param("keyword") String keyword, @Param("offset") int offset, @Param("size") int size);

    /**
     * 统计关键词搜索匹配的商品总数
     * @param keyword 搜索关键词
     * @return 匹配的商品总数
     */
    long countByKeyword(@Param("keyword") String keyword);
}
```
####ProductMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- 声明 MyBatis XML 映射文件的 DTD 约束 -->
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!-- mapper 标签，namespace 对应 ProductMapper 接口的全限定名 -->
<mapper namespace="com.huashan.backend.mapper.ProductMapper">

    <!-- 结果映射：定义数据库列与 Product 实体类属性的映射关系 -->
    <resultMap id="BaseResultMap" type="com.huashan.backend.entity.Product">
            <id property="id" column="id" />
            <result property="name" column="name" />
            <result property="price" column="price" />
            <result property="originalPrice" column="original_price" />
            <result property="description" column="description" />
            <result property="category" column="category" />
            <result property="image" column="image" />
            <result property="stock" column="stock" />
            <result property="sales" column="sales" />
            <result property="status" column="status" />
            <result property="createTime" column="create_time" />
    </resultMap>

    <!-- 可复用的 SQL 片段：定义所有基础列名 -->
    <sql id="Base_Column_List">
        id, name, price, original_price, description, category,
        image, stock, sales, status, create_time
    </sql>

    <!-- 分页查询所有上架商品 -->
    <!-- WHERE status = 1 只查询上架状态的商品 -->
    <!-- ORDER BY create_time DESC 按创建时间倒序排列（最新的在前） -->
    <!-- LIMIT #{offset}, #{size} 实现分页 -->
    <select id="findByPage" resultType="com.huashan.backend.entity.Product">
        SELECT * FROM product WHERE status = 1 ORDER BY create_time DESC LIMIT #{offset}, #{size}
    </select>

    <!-- 统计上架商品总数 -->
    <select id="count" resultType="long">
        SELECT COUNT(*) FROM product WHERE status = 1
    </select>

    <!-- 根据关键词搜索商品（不分页） -->
    <!-- 在商品名称和描述中进行模糊匹配 -->
    <!-- CONCAT('%', #{keyword}, '%') 拼接 LIKE 的通配符 -->
    <select id="searchByKeyword" resultType="com.huashan.backend.entity.Product">
        SELECT * FROM product WHERE status = 1
                                AND (name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))
        ORDER BY create_time DESC
    </select>

    <!-- 按分类分页查询商品 -->
    <!-- 在 status = 1 的基础上，增加 category = #{category} 条件 -->
    <select id="findByCategoryPage" resultType="com.huashan.backend.entity.Product">
        SELECT * FROM product WHERE status = 1 AND category = #{category}
        ORDER BY create_time DESC LIMIT #{offset}, #{size}
    </select>

    <!-- 按分类统计商品数量 -->
    <select id="countByCategory" resultType="long">
        SELECT COUNT(*) FROM product WHERE status = 1 AND category = #{category}
    </select>

    <!-- 全文搜索（MySQL FULLTEXT + ngram，支持中文分词） -->
    <!-- MATCH(name, description) AGAINST(#{keyword} IN NATURAL LANGUAGE MODE) -->
    <!-- 使用自然语言模式进行全文检索，返回相关度评分 -->
    <select id="searchByKeywordFulltext" resultType="com.huashan.backend.entity.Product">
        SELECT *, MATCH(name, description) AGAINST(#{keyword} IN NATURAL LANGUAGE MODE) AS relevance
        FROM product WHERE status = 1
                       AND MATCH(name, description) AGAINST(#{keyword} IN NATURAL LANGUAGE MODE)
        ORDER BY relevance DESC
    </select>

    <!-- LIKE 分页搜索 -->
    <select id="searchByKeywordPage" resultType="com.huashan.backend.entity.Product">
        SELECT * FROM product WHERE status = 1
                                AND (name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))
        ORDER BY create_time DESC LIMIT #{offset}, #{size}
    </select>

    <!-- LIKE 搜索统计数量 -->
    <select id="countByKeyword" resultType="long">
        SELECT COUNT(*) FROM product WHERE status = 1
                                       AND (name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))
    </select>
</mapper>
```
#### ProductService.java（已有）

```java
package com.huashan.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huashan.backend.entity.Product;

public interface ProductService extends IService<Product> {
 /**
     * 分页获取所有上架商品
     * @param page 页码
     * @param size 每页条数
     * @return 分页结果
     */
    PageResult<Product> getProductList(int page, int size);

    /**
     * 按分类分页获取商品
     * @param page 页码
     * @param size 每页条数
     * @param category 分类名称
     * @return 分页结果
     */
    PageResult<Product> getProductListByCategory(int page, int size, String category);

    /**
     * 根据关键词搜索商品（不分页）
     * @param keyword 搜索关键词
     * @return 匹配的商品列表
     */
    List<Product> searchProduct(String keyword);

    /**
     * 根据关键词分页搜索商品
     * @param page 页码
     * @param size 每页条数
     * @param keyword 搜索关键词
     * @return 分页结果
     */
    PageResult<Product> searchProductPaged(int page, int size, String keyword);
}
```

#### ProductServiceImpl.java（已有）

```java
package com.huashan.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huashan.backend.entity.Product;
import com.huashan.backend.mapper.ProductMapper;
import com.huashan.backend.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
// 自动注入商品 Mapper
    @Autowired
    private ProductMapper productMapper;

    /**
     * 分页获取所有上架商品
     * @param page 当前页码（从 1 开始）
     * @param size 每页条数
     * @return 包含商品列表和分页信息的 PageResult 对象
     */
    public PageResult<Product> getProductList(int page, int size) {
        // 计算数据库查询的偏移量：(页码 - 1) * 每页条数
        // 例如第 1 页偏移量为 0，第 2 页偏移量为 10（假设 size=10）
        int offset = (page - 1) * size;
        // 调用 Mapper 方法查询当前页的商品数据
        List<Product> products = productMapper.findByPage(offset, size);
        // 调用 Mapper 方法查询商品总数
        long total = productMapper.count();
        // 封装为 PageResult 对象返回
        return new PageResult<>(products, total, page, size);
    }

    /**
     * 按分类分页获取商品
     * @param page 当前页码
     * @param size 每页条数
     * @param category 分类名称
     * @return 包含该分类下商品列表和分页信息的 PageResult 对象
     */
    public PageResult<Product> getProductListByCategory(int page, int size, String category) {
        // 计算偏移量
        int offset = (page - 1) * size;
        // 查询指定分类下当前页的商品数据
        List<Product> products = productMapper.findByCategoryPage(offset, size, category);
        // 查询指定分类下的商品总数
        long total = productMapper.countByCategory(category);
        // 封装为 PageResult 对象返回
        return new PageResult<>(products, total, page, size);
    }

    /**
     * 根据关键词搜索商品（不分页，返回全部匹配结果）
     * @param keyword 搜索关键词
     * @return 匹配的商品列表
     */
    public List<Product> searchProduct(String keyword) {
        return productMapper.searchByKeyword(keyword);
    }

    /**
     * 根据关键词分页搜索商品
     * @param page 当前页码
     * @param size 每页条数
     * @param keyword 搜索关键词
     * @return 包含搜索结果和分页信息的 PageResult 对象
     */
    public PageResult<Product> searchProductPaged(int page, int size, String keyword) {
        // 计算偏移量
        int offset = (page - 1) * size;
        // 查询当前页的搜索结果
        List<Product> products = productMapper.searchByKeywordPage(keyword, offset, size);
        // 查询搜索匹配的总数
        long total = productMapper.countByKeyword(keyword);
        // 封装为 PageResult 对象返回
        return new PageResult<>(products, total, page, size);
    }
}
```

#### ProductController.java（已有，仅 list 方法）

```java
package com.huashan.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huashan.backend.entity.Product;
import com.huashan.backend.service.ProductService;
import com.huashan.backend.util.PageResult;
import com.huashan.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {

   // 自动注入商品服务
    @Autowired
    private ProductService productService;

    /**
     * 分页获取商品列表，支持按分类筛选
     * 请求方式：GET /api/product/list?page=1&size=10&category=鲜花
     * @param page 页码，默认值为 1
     * @param size 每页条数，默认值为 10
     * @param category 分类名称，可选参数，不传则查询所有商品
     * @return 统一响应结果，包含分页的商品数据
     */
    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String category) {
        // 声明分页结果变量
        PageResult<Product> pageResult;
        // 判断是否传入了分类参数
        if (category != null && !category.isEmpty()) {
            // 如果传入了分类，调用按分类查询的方法
            pageResult = productService.getProductListByCategory(page, size, category);
        } else {
            // 如果没有传入分类，调用查询所有商品的方法
            pageResult = productService.getProductList(page, size);
        }
        // 返回统一封装的分页结果
        return Result.success(pageResult);
    }

    /**
     * 搜索商品，支持分页
     * 请求方式：GET /api/product/search?keyword=玫瑰&page=1&size=10
     * @param keyword 搜索关键词，必填
     * @param page 页码，默认值为 1
     * @param size 每页条数，默认值为 10
     * @return 统一响应结果，包含搜索到的商品数据
     */
    @GetMapping("/search")
    public Result search(@RequestParam String keyword,
                         @RequestParam(defaultValue = "1") int page,
                         @RequestParam(defaultValue = "10") int size) {
        // 兼容旧接口：如果 page=1 且 size>=100，则使用不分页模式返回全部结果
        if (page == 1 && size >= 100) {
            List<Product> list = productService.searchProduct(keyword);
            return Result.success(list);
        }
        // 分页搜索模式
        PageResult<Product> pageResult = productService.searchProductPaged(page, size, keyword);
        return Result.success(pageResult);
    }
}
```


#### product.js（已有）

```js
// 导入封装好的 axios 请求实例
import request from './request'

/**
 * 获取商品列表（分页，可按分类筛选）
 * 发送 GET 请求到 /api/product/list
 * @param {Object} params - 查询参数对象 { page, size, category }
 * @returns {Promise} 返回包含分页商品数据的响应
 */
export function getProductList(params) {
  return request.get('/product/list', { params })
}


/**
 * 搜索商品（支持分页）
 * 发送 GET 请求到 /api/product/search
 * @param {String} keyword - 搜索关键词
 * @param {Number} page - 页码，默认 1
 * @param {Number} size - 每页条数，默认 10
 * @returns {Promise} 返回包含搜索结果的响应
 */
export function searchProduct(keyword, page = 1, size = 10) {
  return request.get('/product/search', { params: { keyword, page, size } })
}

/**
 * 获取启用的分类列表
 * 发送 GET 请求到 /api/category/list
 * @returns {Promise} 返回包含分类列表的响应
 */
export function getCategoryList() {
  return request.get('/category/list')
}

/**
 * 获取最新评价列表
 * 发送 GET 请求到 /api/review/latest
 * @param {Number} limit - 返回条数，默认 8
 * @returns {Promise} 返回包含评价列表的响应
 */
export function getReviews(limit = 8) {
  return request.get('/review/latest', { params: { limit } })
}
```

> ⚠️ 后续步骤二～五会**逐个追加** `getReviews`、`searchProduct`、`getProductDetail`、`getReviewList`。每个步骤只展示该步骤需要追加的代码。
## 一、首页结构分析与 Layout 提取  `35min`
这是本次开发的核心步骤，首页目前是一个"大而全"的单体页面，所有内容都堆在 Home.vue 中。我们将参考 ecommerce 项目的实现，把首页拆分为**公共的 Layout 组件**和**页面特有的内容组件**。
### 1.1 shoptwo 首页现状分析
当前 shoptwo 的 Home.vue 包含以下 7 个区块：
- **信任标签栏**：全宽展示，"优秀企业""20年品牌"等标识
- **轮播图区域**：全宽展示，4 张鲜花主题轮播图
- **分类导航**：全宽展示，圆形图标 + 分类名，点击筛选商品
- **搜索结果提示**：条件显示，搜索时展示关键词和结果数
- **鲜花推荐区**：居中展示，大小卡片组合的推荐网格
- **商品展示区**：居中展示，5 列商品网格 + 分页
- **晒单评价区**：居中展示，4 列评价卡片网格
### 1.2 与 ecommerce 首页对比
ecommerce 项目采用了 Layout + 子页面的架构。对比两者的首页结构：
| 区块 | shoptwo（当前） | ecommerce（参考） 
| 顶部欢迎条 | ❌ 无 | ✅ Layout 中 
| Logo + 品牌名 | ❌ 无 | ✅ Layout 导航栏中 
| 导航菜单 | ❌ 无 | ✅ Layout 导航栏中 
| 搜索框 | ❌ 无 | ✅ Layout 导航栏中 
| 用户操作区 | ❌ 无 | ✅ Layout 导航栏中 
| 信任标签栏 | ✅ Home.vue 中 | ✅ Home.vue 中 
| 轮播图 | ✅ Home.vue 中 | ✅ Home.vue 中 
| 分类导航 | ✅ Home.vue 中 | ✅ Home.vue 中 
| 商品展示 | ✅ Home.vue 中 | ✅ Home.vue 中 
| 晒单评价 | ✅ Home.vue 中 | ✅ Home.vue 中 
| 服务保障条 | ❌ 无 | ✅ Layout 中 
| 底部版权 | ❌ 无 | ✅ Layout 中 
### 1.3 提取思路：识别"公共部分"和"页面特有部分"
从对比中可以清晰看出，shoptwo 首页缺少的是**页面框架**（导航栏、底部版权），而这些正是 ecommerce 中 ShopLayout 组件提供的。提取原则如下：
| 区块 | 是否公共 | 放置位置 | 理由 
| 顶部欢迎条 | 是 | Layout | 所有页面共享 
| Logo + 品牌名 | 是 | Layout 导航栏 | 所有页面共享 
| 导航菜单 | 是 | Layout 导航栏 | 所有页面共享 
| 搜索框 | 是 | Layout 导航栏 | 所有页面共享 
| 用户操作区 | 是 | Layout 导航栏 | 所有页面共享 
| 信任标签栏 | 是 | Layout | 所有页面共享品牌信任感 
| 轮播图 | 否 | Home.vue | 仅首页需要 
| 分类导航 | 否 | Home.vue | 仅首页需要（Layout 导航栏有简化版） 
| 商品展示 | 否 | Home.vue | 仅首页需要 
| 晒单评价 | 否 | Home.vue | 仅首页需要 
| 服务保障条 | 是 | Layout | 所有页面共享 
| 底部版权 | 是 | Layout | 所有页面共享 
### 1.4 Layout 组件的结构设计
参考 ecommerce 的 ShopLayout.vue，Layout 组件包含 5 个部分：
> **ShopLayout 组件结构：5 大区域包裹所有子页面**
> ```mermaid
> 
graph TD
A[ShopLayout.vue] --> B[顶部欢迎条]
A --> C[主导航栏]
A --> D[主内容区 router-view]
A --> E[服务保障条]
A --> F[底部版权]
C --> C1[Logo + 品牌名]
C --> C2[导航链接]
C --> C3[搜索框]
C --> C4[用户操作]
D --> D1[Home.vue]
D --> D2[Login.vue]
D --> D3[ProductDetail.vue]
D --> D4[ReviewList.vue]
> ```
### 1.5 操作步骤概览

Layout 提取分三步，每步都是**从现有 Home.vue 中剪切代码、粘贴到新文件**：

| 步骤 | 操作 | 产出 |
|------|------|------|
| ① | 新建 `ShopLayout.vue`，从 Home.vue 剪切头部/服务条/页脚 → 粘贴进去 | ShopLayout.vue |
| ② | 清理 Home.vue：删除已移走的区块，去掉 `full-width` 类 | 精简版 Home.vue |
| ③ | 修改 `router/index.js`，改为嵌套路由 | 路由配置 |

> **核心原则：所有代码都从 Home.vue 中原样剪切，类名、样式、数据全部保持一致，不重新发明。**

---

### 1.6 第一步：创建 ShopLayout.vue（完整代码，可直接复制粘贴）

> **文件：** `frontend/src/views/shop/ShopLayout.vue`（新建）

将以下完整代码复制粘贴到新文件中。这段代码是从 Home.vue 中提取的**头部 + 信任标签 + 服务保障 + 页脚**，并在中间插入 `<router-view />` 作为子页面出口：

```vue
<template>
  <div class="shop-layout">
    <!-- ==================== 头部：顶部欢迎条 32px #A01020 ==================== -->
    <div class="header-top">
      <div class="header-top__inner">
        <span class="header-top__welcome">欢迎来到花之恋鲜花电商平台！</span>
        <div class="header-top__user">
          <span class="header-top__greeting">Hi, {{ username }}</span>
          <span class="header-top__sep">|</span>
          <span class="header-top__logout" @click="handleLogout">退出</span>
        </div>
      </div>
    </div>

    <!-- ==================== 头部：主导航 72px #C71526 ==================== -->
    <div class="header-main">
      <div class="header-main__inner">
        <div class="header-logo" @click="$router.push('/shop')">
          <span class="header-logo__icon">🌸</span>
          <div class="header-logo__text">
            <span class="header-logo__cn">花之恋</span>
            <span class="header-logo__en">FLORA LOVE</span>
          </div>
        </div>

        <div class="header-nav">
          <span
            v-for="nav in headerNavs"
            :key="nav.name"
            class="header-nav__item"
            :class="{ 'header-nav__item--active': isNavActive(nav) }"
            @click="handleNavClick(nav)"
          >{{ nav.name }}</span>
        </div>

        <div class="header-search">
          <input
            v-model="searchInput"
            class="header-search__input"
            placeholder="搜索鲜花、花束..."
            @keyup.enter="handleSearch"
          />
        </div>

        <div class="header-actions">
          <span class="header-actions__item" @click="$router.push('/shop/cart')">🛒 购物车</span>
          <span class="header-actions__item" @click="$router.push('/shop/orders')">📄 我的订单</span>
          <span class="header-actions__item" @click="$router.push('/shop/profile')">👤 {{ username }}</span>
        </div>
      </div>
    </div>

    <!-- ==================== 信任标签栏 ==================== -->
    <div class="trust-bar">
      <div class="trust-bar__inner">
        <span v-for="item in trustBadges" :key="item.label" class="trust-badge">
          <span class="trust-badge__icon">{{ item.emoji }}</span>
          <span class="trust-badge__text">{{ item.label }}</span>
        </span>
      </div>
    </div>

    <!-- ==================== 主内容区：子页面在此渲染 ==================== -->
    <main class="layout-main">
      <router-view />
    </main>

    <!-- ==================== 服务保障栏 ==================== -->
    <div class="service-bar">
      <div class="service-bar__inner">
        <div v-for="svc in serviceFeatures" :key="svc.title" class="service-item">
          <span class="service-item__icon">{{ svc.emoji }}</span>
          <div class="service-item__text">
            <span class="service-item__title">{{ svc.title }}</span>
            <span class="service-item__desc">{{ svc.desc }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- ==================== 页脚 ==================== -->
    <div class="footer">
      <div class="footer__inner">
        <div class="footer__main">
          <div class="footer-logo">
            <span class="footer-logo__icon">🌸</span>
            <span class="footer-logo__cn">花之恋</span>
            <span class="footer-logo__en">FLORA LOVE</span>
          </div>
          <div v-for="col in footerColumns" :key="col.title" class="footer-col">
            <h4 class="footer-col__title">{{ col.title }}</h4>
            <span v-for="link in col.links" :key="link" class="footer-col__link">{{ link }}</span>
          </div>
        </div>
        <div class="footer__bottom">
          <p class="footer__copyright">© 2026 花之恋鲜花电商 - 基于SpringBoot AI电商平台</p>
          <p class="footer__credit">华杉科技(河南)有限公司 × 河南工业大学 Java实训项目</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getCategoryList } from '../../api/product'

export default {
  name: 'ShopLayout',

  data() {
    return {
      username: '张三',
      searchInput: '',

      headerNavs: [
        { name: '首页', link: '/shop', active: true },
        { name: '鲜花', link: '/shop?category=' + encodeURIComponent('鲜花'), active: false },
        { name: '永生花', link: '/shop?category=' + encodeURIComponent('永生花'), active: false },
        { name: '花束', link: '/shop?category=' + encodeURIComponent('花束'), active: false },
        { name: '花篮', link: '/shop?category=' + encodeURIComponent('花篮'), active: false },
        { name: '绿植', link: '/shop?category=' + encodeURIComponent('绿植'), active: false }
      ],

      trustBadges: [
        { emoji: '🏆', label: '优秀企业' },
        { emoji: '🏅', label: '20年品牌' },
        { emoji: '🚚', label: '全国送花' },
        { emoji: '🛡️', label: '退赔承诺' },
        { emoji: '📷', label: '最近99+条评价' }
      ],

      serviceFeatures: [
        { emoji: '✅', title: '正品保障', desc: '品质鲜花 假一赔十' },
        { emoji: '🚚', title: '免费配送', desc: '指定区域 免费送达' },
        { emoji: '🎨', title: '专业花艺', desc: '花艺师亲手制作' },
        { emoji: '💝', title: '贴心售后', desc: '售后无忧 全程呵护' }
      ],

      footerColumns: [
        { title: '购物指南', links: ['购物流程', '会员介绍', '常见问题'] },
        { title: '配送方式', links: ['上门自提', '快递运输', '配送范围'] },
        { title: '售后服务', links: ['退换政策', '退换流程', '价格保护'] },
        { title: '关于我们', links: ['平台介绍', '联系我们', '招贤纳士'] }
      ]
    }
  },

  methods: {
    // 从 API 加载分类，动态更新导航链接
    async loadNavCategories() {
      try {
        const res = await getCategoryList()
        const list = Array.isArray(res.data) ? res.data : []
        if (list.length > 0) {
          this.headerNavs = [
            { name: '首页', link: '/shop', active: true },
            ...list.map(cat => ({
              name: cat.name,
              link: '/shop?category=' + encodeURIComponent(cat.name),
              active: false
            }))
          ]
        }
      } catch {
        // 降级：保持 data 中已定义的硬编码导航
      }
    },

    // 判断导航项是否激活
    isNavActive(nav) {
      if (nav.name === '首页') {
        return this.$route.path === '/shop' && !this.$route.query.category
      }
      const navCat = nav.link.includes('category=')
        ? decodeURIComponent(nav.link.split('category=')[1])
        : ''
      return this.$route.query.category === navCat
    },

    // 导航点击
    handleNavClick(nav) {
      this.$router.push(nav.link)
    },

    // 搜索：回车触发，通过 query 传递关键词到首页
    handleSearch() {
      const q = this.searchInput.trim()
      const query = {}
      if (q) query.keyword = q
      this.$router.push({ path: '/shop', query })
    },

    // 退出登录
    handleLogout() {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      this.$router.push('/')
    }
  },

  created() {
    const stored = localStorage.getItem('username')
    if (stored) this.username = stored
    this.loadNavCategories()
  }
}
</script>

<style scoped>
/* ==================== 全局 ==================== */
.shop-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}
.layout-main {
  flex: 1;
}

/* ==================== 顶部欢迎条 32px #A01020 ==================== */
.header-top {
  background: #A01020;
  height: 32px;
}
.header-top__inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
}
.header-top__welcome {
  font-size: 12px;
  color: #fff;
  font-family: 'Microsoft YaHei', sans-serif;
}
.header-top__user {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}
.header-top__greeting { color: #fff; }
.header-top__sep { color: rgba(255,255,255,0.5); }
.header-top__logout {
  color: #66B1FF;
  cursor: pointer;
  text-decoration: underline;
}
.header-top__logout:hover { color: #fff; }

/* ==================== 主导航 72px #C71526 ==================== */
.header-main {
  background: #C71526;
  height: 72px;
  box-shadow: 0 2px 12px rgba(199,21,38,0.3);
}
.header-main__inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 20px;
  gap: 20px;
}
.header-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  flex-shrink: 0;
}
.header-logo__icon { font-size: 24px; line-height: 1; }
.header-logo__text { display: flex; flex-direction: column; }
.header-logo__cn {
  font-size: 26px;
  font-weight: 700;
  color: #fff;
  line-height: 1.2;
  font-family: 'Microsoft YaHei', sans-serif;
}
.header-logo__en {
  font-size: 10px;
  color: rgba(255,255,255,0.8);
  font-family: 'Microsoft YaHei', sans-serif;
}
.header-nav {
  display: flex;
  align-items: center;
  gap: 18px;
  flex-shrink: 0;
}
.header-nav__item {
  font-size: 15px;
  color: rgba(255,255,255,0.85);
  cursor: pointer;
  text-decoration: underline;
  white-space: nowrap;
  font-family: 'Microsoft YaHei', sans-serif;
}
.header-nav__item--active {
  font-weight: 700;
  color: #fff;
}
.header-nav__item:hover { color: #fff; }
.header-search {
  flex: 1;
  max-width: 200px;
}
.header-search__input {
  width: 100%;
  height: 34px;
  border-radius: 18px;
  border: 1px solid rgba(255,255,255,0.3);
  background: rgba(255,255,255,0.2);
  padding: 0 11px;
  font-size: 14px;
  color: #fff;
  outline: none;
  box-sizing: border-box;
  font-family: 'Arial', sans-serif;
}
.header-search__input::placeholder { color: rgba(255,255,255,0.6); }
.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}
.header-actions__item {
  font-size: 14px;
  color: rgba(255,255,255,0.85);
  cursor: pointer;
  text-decoration: underline;
  white-space: nowrap;
  padding: 8px 16px;
  font-family: 'Microsoft YaHei', sans-serif;
}
.header-actions__item:hover { color: #fff; }

/* ==================== 信任标签栏 ==================== */
.trust-bar {
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
}
.trust-bar__inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 49px;
  gap: 48px;
  padding: 0 20px;
}
.trust-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}
.trust-badge__icon { font-size: 14px; line-height: 1; }
.trust-badge__text {
  font-size: 14px;
  color: #666;
  font-family: 'Microsoft YaHei', sans-serif;
}

/* ==================== 服务保障栏 ==================== */
.service-bar {
  background: #FFF5F5;
  border-top: 1px solid #FFE0E0;
  border-bottom: 1px solid #FFE0E0;
}
.service-bar__inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-around;
  align-items: center;
  height: 107px;
  padding: 0 20px;
}
.service-item {
  display: flex;
  align-items: center;
  gap: 14px;
}
.service-item__icon { font-size: 27px; line-height: 1; }
.service-item__text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.service-item__title {
  font-size: 16px;
  font-weight: 700;
  color: #C71526;
  font-family: 'Microsoft YaHei', sans-serif;
}
.service-item__desc {
  font-size: 12px;
  color: #999;
  font-family: 'Microsoft YaHei', sans-serif;
}

/* ==================== 页脚 ==================== */
.footer {
  background: #2B2B2B;
  padding: 32px 0 20px;
}
.footer__inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}
.footer__main {
  display: flex;
  gap: 80px;
  margin-bottom: 30px;
}
.footer-logo {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  flex-shrink: 0;
}
.footer-logo__icon { font-size: 28px; margin-bottom: 6px; }
.footer-logo__cn {
  font-size: 26px;
  font-weight: 700;
  color: #fff;
  font-family: 'Microsoft YaHei', sans-serif;
}
.footer-logo__en {
  font-size: 10px;
  color: rgba(255,255,255,0.6);
  margin-top: 2px;
  font-family: 'Microsoft YaHei', sans-serif;
}
.footer-col {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.footer-col__title {
  font-size: 14px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 4px 0;
  font-family: 'Microsoft YaHei', sans-serif;
  line-height: 2.5;
}
.footer-col__link {
  font-size: 12px;
  color: #999;
  cursor: pointer;
  font-family: 'Microsoft YaHei', sans-serif;
}
.footer-col__link:hover { color: #fff; }
.footer__bottom {
  border-top: 1px solid rgba(255,255,255,0.08);
  padding-top: 20px;
  text-align: center;
}
.footer__copyright {
  font-size: 11px;
  color: #666;
  margin: 0 0 4px 0;
  font-family: 'Microsoft YaHei', sans-serif;
}
.footer__credit {
  font-size: 12px;
  color: #666;
  margin: 0;
  font-family: 'Microsoft YaHei', sans-serif;
}

/* ==================== 响应式 ==================== */
@media (max-width: 1024px) {
  .header-nav { gap: 10px; }
  .header-nav__item { font-size: 13px; }
  .header-search { max-width: 140px; }
  .header-actions__item { font-size: 12px; padding: 4px 8px; }
  .header-logo__cn { font-size: 20px; }
  .header-logo__en { font-size: 8px; }
  .trust-bar__inner { gap: 24px; }
  .service-bar__inner { height: auto; padding: 24px 20px; flex-wrap: wrap; gap: 24px; }
  .footer__main { gap: 40px; flex-wrap: wrap; }
}
@media (max-width: 768px) {
  .header-main__inner { flex-wrap: wrap; height: auto; padding: 12px 20px; gap: 12px; }
  .header-nav { order: 3; width: 100%; justify-content: center; }
  .header-search { order: 4; max-width: 100%; width: 100%; }
  .header-actions { order: 2; }
  .header-top__welcome { display: none; }
  .trust-bar__inner { flex-wrap: wrap; gap: 16px; height: auto; padding: 12px 10px; }
  .service-bar__inner { flex-direction: column; align-items: flex-start; }
  .footer__main { flex-direction: column; gap: 24px; }
}
</style>
```

#### 关键逻辑解析

| 逻辑 | 说明 |
|------|------|
| `loadNavCategories()` | 从后端 `/api/category/list` 加载分类，动态替换 `headerNavs`；失败时保持 data 中的硬编码默认值 |
| `isNavActive()` | 首页：仅在无 category 参数时激活；分类页：精确匹配 `nav.link` 中的 `category=` 参数值 |
| `handleSearch()` | 将搜索关键词写入 `$route.query.keyword`，Home.vue 通过 watch 响应 |
| `handleLogout()` | 清除 localStorage 的 token 和 username，跳转到登录页 `/` |
| `<router-view />` | 放在 `<main class="layout-main">` 中，子页面（Home.vue / Login.vue 等）在此渲染 |

---

### 1.7 第二步：精简 Home.vue

创建 ShopLayout.vue 后，需要从 Home.vue 中**删除已移走的区块**。具体要删除以下 4 类内容：

#### 模板删除清单

从 `<template>` 中删除以下整段注释+HTML（按原文件顺序）：

| 序号 | 删除内容 | 对应原注释标记 |
|------|---------|--------------|
| ① | 顶部欢迎条 | `<!-- ==================== 头部区域 ==================== -->` 到 `</div>`（header-main 结束） |
| ② | 信任标签栏 | `<!-- ==================== 1. 信任标签栏（全宽） ==================== -->` 整段 |
| ③ | 服务保障栏 | `<!-- ==================== 8. 服务保障栏（全宽） ==================== -->` 整段 |
| ④ | 页脚 | `<!-- ==================== 9. 页脚（全宽） ==================== -->` 整段 |

#### 模板修改：去掉 `full-width` 类

Home.vue 在 Layout 的 `<router-view />` 中渲染，其父容器 `.layout-main` 没有宽度限制，所有区块天然全宽。因此需要：

- 删除所有 `full-width` 类名（搜索替换 `full-width` 为空字符串）
- 轮播图 `class="banner-section full-width"` → `class="banner-section"`
- 分类导航 `class="category-nav full-width"` → `class="category-nav"`
- 搜索提示 `class="search-hint full-width"` → `class="search-hint"`

#### Script 删除清单

从 `data()` 的 `return {}` 中删除以下属性（整行或整块）：

```js
// 删除这些：
username: '张三',
searchInput: '',
keyword: '',        // ← 注意：keyword 仍需要！从 route query 同步
headerNavs: [ ... ], // 整块数组
trustBadges: [ ... ], // 整块数组
serviceFeatures: [ ... ], // 整块数组
footerColumns: [ ... ], // 整块数组
```

> ⚠️ **`keyword` 保留**：`keyword` 是 Home.vue 从 `$route.query.keyword` 同步来的，用于判断是否显示搜索提示条和调用 `searchProduct()`，不能删除。

从 `methods` 中删除：

```js
handleSearch() { ... },   // 移到 Layout
handleLogout() { ... },   // 移到 Layout
```

从 `created()` 中删除：

```js
const stored = localStorage.getItem('username')
if (stored) this.username = stored
```

#### Style 删除清单

从 `<style scoped>` 中删除以下整段 CSS（按注释标记）：

| 删除内容 | 原注释标记 |
|---------|-----------|
| 全局重置 `.shop-home` | 改为 `.shop-home { background: #f5f5f5; }` |
| 全宽容器 `.full-width` | 整段删除 |
| 头部：顶部欢迎条 | `/* ==================== 头部：顶部欢迎条 ==================== */` 整段 |
| 头部：主导航 | `/* ==================== 头部：主导航 ==================== */` 整段 |
| 信任标签栏 | `/* ==================== 1. 信任标签栏 ==================== */` 整段 |
| 服务保障栏 | `/* ==================== 8. 服务保障栏 ==================== */` 整段 |
| 页脚 | `/* ==================== 9. 页脚 ==================== */` 整段 |
| 响应式中对应的部分 | media query 中 header/trust-bar/service/footer 的规则 |

#### 精简后的 Home.vue 骨架确认

精简后 Home.vue 的 `<template>` 结构应为：

```
<div class="shop-home">
  <!-- 2. 轮播图区域 -->
  <!-- 3. 分类导航 -->
  <!-- 4. 搜索结果提示（条件显示） -->
  <!-- 5. 鲜花推荐区 -->
  <!-- 6. 商品展示区 -->
  <!-- 7. 晒单评价区 -->
</div>
```

---

### 1.8 第三步：修改路由为嵌套路由

> **文件：** `frontend/src/router/index.js`（修改）

**修改前（扁平路由）**：
```js
import { createRouter, createWebHashHistory } from 'vue-router'
const ShopHome = () => import('../views/shop/HomeView.vue')
const ShopLogin = () => import('../views/shop/Login.vue')
const routes = [
  { path: '/shop', name:'ShopHome', component: ShopHome },
  { path: '/', name:'ShopLogin', component: ShopLogin }
]
```

**修改后（嵌套路由）**：
```js
import { createRouter, createWebHashHistory } from 'vue-router'

const ShopLayout = () => import('../views/shop/ShopLayout.vue')
const ShopHome = () => import('../views/shop/Home.vue')
const ShopLogin = () => import('../views/shop/Login.vue')

const routes = [
  {
    path: '/shop',
    component: ShopLayout,
    children: [
      { path: '', name: 'ShopHome', component: ShopHome },
      { path: 'login', name: 'ShopLogin', component: ShopLogin }
    ]
  },
  { path: '/', redirect: '/shop' }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})
export default router
```

#### 嵌套路由配置解析

| 配置 | 含义 | 实际 URL |
|------|------|----------|
| `path: '/shop'` | 父路由路径 | `#/shop` |
| `component: ShopLayout` | 父级渲染 Layout 组件 | Layout 的 `<router-view />` 在此 |
| `children: [{ path: '' }]` | 子路由路径为空，拼接后为 `/shop` | `#/shop` 渲染 Home.vue |
| `children: [{ path: 'login' }]` | 子路由路径为 `login`，拼接后为 `/shop/login` | `#/shop/login` 渲染 Login.vue |
| `{ path: '/', redirect: '/shop' }` | 根路径重定向 | `#/` → `#/shop` |

#### 嵌套路由渲染流程

```
用户访问 /shop
  → Vue Router 匹配父路由 path: '/shop'
    → 渲染 ShopLayout.vue（头部 + 信任条 + 服务条 + 页脚）
      → <router-view /> 匹配子路由 path: ''
        → 渲染 Home.vue（轮播图 + 分类 + 商品 + 评价）
          → 最终：Layout 外壳包裹 Home 内容
```

---

### 1.9 预览验证

按顺序执行后刷新前端页面：

| 序号 | 验证点 | 预期结果 |
|------|--------|---------|
| 1 | 页面顶部 | 出现欢迎条（白字红底）+ 导航栏（Logo + 菜单 + 搜索框 + 用户操作） |
| 2 | Logo | `🌸 花之恋` + `FLORA LOVE` 显示正常，点击可回到首页 |
| 3 | 导航链接 | 首页（选中加粗）+ 从 API 加载的分类链接正常显示 |
| 4 | 搜索框 | 圆角半透明搜索框在导航栏右侧，输入关键词回车可搜索 |
| 5 | 信任标签 | 导航栏下方出现 5 个信任标签（🏆优秀企业 等） |
| 6 | 首页内容 | 轮播图、分类导航、商品网格、评价卡片正常显示 |
| 7 | 服务保障 | 评价区下方出现 ✅正品保障 🚚免费配送 🎨专业花艺 💝贴心售后 |
| 8 | 页脚 | 深色背景（`#2B2B2B`）+ Logo + 4 列链接 + 版权信息 |
| 9 | 登录页 | 访问 `#/shop/login`，登录页被 Layout 包裹（有导航栏和底部） |
| 10 | 分类筛选 | 点击导航栏分类链接，首页商品列表按分类筛选 |
| 11 | 搜索功能 | 在搜索框输入关键词按回车，首页显示搜索结果 |

### 1.10 常见问题排查

| 现象 | 可能原因 | 解决方法 |
|------|---------|---------|
| 页面完全空白 | ShopLayout.vue 未创建或路径错误 | 确认文件在 `src/views/shop/ShopLayout.vue` |
| 只有 Layout 没有子页面 | 缺少 `<router-view />` | Layout 模板中 `<main class="layout-main">` 内必须有 `<router-view />` |
| 子页面显示但 Layout 消失 | 子路由未配在 children 中 | 检查 router 的 children 数组 |
| 轮播图/分类栏超出屏幕 | Home.vue 未删除 `full-width` 类 | 搜索替换删除所有 `full-width` |
| 样式丢失（颜色/字体不对） | CSS 未从 Home.vue 完整复制 | 对照 1.6 节检查 ShopLayout.vue 的 `<style>` |
| 导航链接点击无反应 | `isNavActive` 逻辑错误 | 确保分类名精确匹配（注意 URL 编码） |
| 搜索后 URL 变了但列表没变 | Home.vue 删除了 `keyword` watch | Home.vue 必须保留 `$route.query.keyword` 的 watch |
| `getCategoryList is not exported` | product.js 缺少该函数 | 检查 `src/api/product.js` 是否 export 了 `getCategoryList` |
## 二、首页后端完善  `40min`

首页需要 3 类数据：商品列表、商品分类、最新评价。Product 模块已完整实现（§0.3），本节补全 Category 和 Review 模块的后端全链路，以及 Product 的搜索功能。

### 2.1 Category 模块（全链路实现）

#### ① CategoryMapper.java（追加方法）

> **文件：** `backend/src/main/java/com/huashan/backend/mapper/CategoryMapper.java`

在已有接口中追加方法：

```java
/**
 * 查询所有启用的分类，按 sort_order 升序
 */
List<Category> findAllEnabled();
```

#### ② CategoryMapper.xml（追加 SQL）

> **文件：** `backend/src/main/resources/com/huashan/backend/mapper/CategoryMapper.xml`

在已有 `<mapper>` 标签内追加：

```xml
<!-- 查询所有启用的分类 -->
<select id="findAllEnabled" resultType="com.huashan.backend.entity.Category">
    select <include refid="Base_Column_List"/> from category
    where status = 1 order by sort_order asc
</select>
```

#### ③ CategoryService.java（追加方法）

> **文件：** `backend/src/main/java/com/huashan/backend/service/CategoryService.java`

```java
/**
 * 获取所有启用的分类
 */
List<Category> getAllCategories();
```

#### ④ CategoryServiceImpl.java（覆盖已有空壳）

> **文件：** `backend/src/main/java/com/huashan/backend/service/impl/CategoryServiceImpl.java`

用以下完整代码覆盖已有的空壳文件：

```java
package com.huashan.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huashan.backend.entity.Category;
import com.huashan.backend.mapper.CategoryMapper;
import com.huashan.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.findAllEnabled();
    }
}
```

#### ⑤ CategoryController.java（新建）

> **文件：** `backend/src/main/java/com/huashan/backend/controller/CategoryController.java`（新建）

```java
package com.huashan.backend.controller;

import com.huashan.backend.entity.Category;
import com.huashan.backend.service.CategoryService;
import com.huashan.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取全部分类
     * GET /api/category/list
     */
    @GetMapping("/list")
    public Result list() {
        List<Category> list = categoryService.getAllCategories();
        return Result.success(list);
    }
}
```

### 2.2 Review 模块（全链路实现）

#### ① ReviewMapper.java（追加方法）

> **文件：** `backend/src/main/java/com/huashan/backend/mapper/ReviewMapper.java`

在已有接口中追加方法：

```java
/**
 * 查询最新 N 条评价
 */
List<Review> findLatest(@Param("limit") int limit);
```

#### ② ReviewMapper.xml（追加 SQL）

> **文件：** `backend/src/main/resources/com/huashan/backend/mapper/ReviewMapper.xml`

在已有 `<mapper>` 标签内追加：

```xml
<!-- 查询最新评价列表（按创建时间倒序，限制条数） -->
<select id="findLatest" resultType="com.huashan.backend.entity.Review">
    select <include refid="Base_Column_List"/> from review
    order by create_time desc limit #{limit}
</select>
```

#### ③ ReviewService.java（追加方法）

> **文件：** `backend/src/main/java/com/huashan/backend/service/ReviewService.java`

```java
/**
 * 获取最新 N 条评价
 */
List<Review> getLatestReviews(int limit);
```

#### ④ ReviewServiceImpl.java（覆盖已有空壳）

> **文件：** `backend/src/main/java/com/huashan/backend/service/impl/ReviewServiceImpl.java`

用以下完整代码覆盖已有的空壳文件：

```java
package com.huashan.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huashan.backend.entity.Review;
import com.huashan.backend.mapper.ReviewMapper;
import com.huashan.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review>
        implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public List<Review> getLatestReviews(int limit) {
        return reviewMapper.findLatest(limit);
    }
}
```

#### ⑤ ReviewController.java（新建，本节只需 latest 方法）

> **文件：** `backend/src/main/java/com/huashan/backend/controller/ReviewController.java`（新建）

```java
package com.huashan.backend.controller;

import com.huashan.backend.entity.Review;
import com.huashan.backend.service.ReviewService;
import com.huashan.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 获取最新评价
     * GET /api/review/latest?limit=8
     */
    @GetMapping("/latest")
    public Result latest(@RequestParam(defaultValue = "8") int limit) {
        List<Review> list = reviewService.getLatestReviews(limit);
        return Result.success(list);
    }
}
```

> ⚠️ `list` 方法将在 §4 评价列表页追加。

### 2.3 Product 搜索功能（已有 Controller 追加方法）

> **文件：** `backend/src/main/java/com/huashan/backend/controller/ProductController.java`

在已有的 `ProductController` 类中追加（Service 层的 `searchProductPaged` 方法已在 §0.3 实现）：

```java
/**
 * 搜索商品
 * GET /api/product/search?keyword=玫瑰&page=1&size=10
 */
@GetMapping("/search")
public Result search(@RequestParam String keyword,
                     @RequestParam(defaultValue = "1") int page,
                     @RequestParam(defaultValue = "10") int size) {
    PageResult<Product> pageResult = productService.searchProductPaged(keyword, page, size);
    return Result.success(pageResult);
}
```

### 2.4 前端 product.js 追加 API 函数

> **文件：** `frontend/src/api/product.js`

在文件末尾追加 2 个函数（当前累计 5 个）：

```js
/**
 * 搜索商品
 * @param {String} keyword - 搜索关键词
 * @param {Number} page - 页码
 * @param {Number} size - 每页条数
 */
export function searchProduct(keyword, page = 1, size = 10) {
  return request.get('/product/search', { params: { keyword, page, size } })
}

/**
 * 获取最新评价
 * @param {Number} limit - 获取条数，默认 8
 */
export function getReviews(limit = 8) {
  return request.get('/review/latest', { params: { limit } })
}
```

> `getCategoryList` 已在 §0.3 初始状态存在，无需重复添加。

### 2.5 预览验证

| 序号 | 验证点 | 预期结果 |
|------|--------|---------|
| 1 | CategoryMapper | 追加 `findAllEnabled()` 方法 |
| 2 | CategoryMapper.xml | 追加 SQL 查询 |
| 3 | CategoryServiceImpl | 实现 `getAllCategories()` |
| 4 | CategoryController | `GET /api/category/list` 返回分类数组 |
| 5 | ReviewMapper | 追加 `findLatest()` 方法 |
| 6 | ReviewMapper.xml | 追加 SQL 查询 |
| 7 | ReviewServiceImpl | 实现 `getLatestReviews()` |
| 8 | ReviewController | `GET /api/review/latest?limit=8` 返回评价数组 |
| 9 | ProductController | 追加 `search()` 方法 |
| 10 | product.js | 已追加 `searchProduct`、`getReviews`，累计 5 个函数 |
| 11 | 首页 | 分类导航显示、评价区显示、搜索功能正常 |

---

## 三、商品详情页  `35min`

### 3.1 后端：ProductController 追加详情方法

> **文件：** `backend/src/main/java/com/huashan/backend/controller/ProductController.java`

在已有类中追加：

```java
/**
 * 获取商品详情
 * GET /api/product/{id}
 */
@GetMapping("/{id}")
public Result detail(@PathVariable Integer id) {
    Product product = productService.getById(id);
    if (product == null || product.getStatus() != 1) {
        return Result.error("商品不存在或已下架");
    }
    return Result.success(product);
}
```

### 3.2 前端：product.js 追加 + ProductDetail.vue 新建

#### 3.2.1 追加 API 函数

> **文件：** `frontend/src/api/product.js`（末尾追加，累计 6 个）

```js
/**
 * 获取商品详情
 * @param {Number} id - 商品ID
 */
export function getProductDetail(id) {
  return request.get(`/product/${id}`)
}
```

#### 3.2.2 新建 ProductDetail.vue

> **文件：** `frontend/src/views/shop/ProductDetail.vue`（新建）

```vue
<template>
  <div class="product-detail-page">
    <el-breadcrumb separator="/" class="detail-breadcrumb">
      <el-breadcrumb-item :to="{ path: '/shop' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item v-if="product && product.category">{{ product.category }}</el-breadcrumb-item>
      <el-breadcrumb-item>商品详情</el-breadcrumb-item>
    </el-breadcrumb>

    <div v-if="loading" class="detail-skeleton">
      <el-skeleton animated>
        <template #template>
          <div class="sk-layout">
            <el-skeleton-item variant="image" style="width:420px;height:420px" />
            <div style="flex:1;padding-left:40px">
              <el-skeleton-item variant="text" style="width:60%;height:32px" />
              <el-skeleton-item variant="text" style="width:40%;margin-top:16px" />
              <el-skeleton-item variant="text" style="width:30%;margin-top:12px" />
              <el-skeleton-item variant="text" style="width:80%;margin-top:32px" />
            </div>
          </div>
        </template>
      </el-skeleton>
    </div>

    <div v-else-if="product" class="detail-layout">
      <div class="detail-left">
        <el-image
          :src="product.image || 'https://picsum.photos/seed/' + product.id + '/420/420'"
          :preview-src-list="[product.image || 'https://picsum.photos/seed/' + product.id + '/800/800']"
          fit="cover" class="detail-image"
        />
      </div>
      <div class="detail-right">
        <h1 class="detail-name">{{ product.name }}</h1>
        <div class="detail-tags">
          <el-tag type="danger" size="small">鲜花速递</el-tag>
          <el-tag type="warning" size="small">免费配送</el-tag>
        </div>
        <div class="detail-price">
          <span class="detail-price__current">¥{{ product.price }}</span>
          <span v-if="product.originalPrice" class="detail-price__original">¥{{ product.originalPrice }}</span>
          <el-tag v-if="product.originalPrice" type="danger" size="small">{{ discountText }}</el-tag>
        </div>
        <div class="detail-meta">
          <div class="detail-meta__row">
            <span class="detail-meta__label">分类</span>
            <span class="detail-meta__value">{{ product.category || '-' }}</span>
          </div>
          <div class="detail-meta__row">
            <span class="detail-meta__label">库存</span>
            <span class="detail-meta__value" :class="{ 'text-danger': product.stock <= 10 }">
              {{ product.stock > 0 ? '有货（' + product.stock + '件）' : '暂时缺货' }}
            </span>
          </div>
          <div v-if="product.description" class="detail-meta__row">
            <span class="detail-meta__label">描述</span>
            <span class="detail-meta__value detail-meta__desc">{{ product.description }}</span>
          </div>
        </div>
        <div class="detail-quantity">
          <span class="detail-quantity__label">购买数量</span>
          <el-input-number v-model="quantity" :min="1" :max="99" size="large" />
        </div>
        <div class="detail-actions">
          <el-button type="danger" size="large" round @click="handleBuy">立即购买</el-button>
          <el-button size="large" round @click="handleAddCart">加入购物车</el-button>
        </div>
      </div>
    </div>

    <el-empty v-else description="商品不存在或已下架">
      <el-button type="primary" @click="$router.push('/shop')">返回首页</el-button>
    </el-empty>
  </div>
</template>

<script>
import { getProductDetail } from '../../api/product'

export default {
  name: 'ProductDetail',
  data() {
    return { product: null, loading: true, quantity: 1 }
  },
  computed: {
    discountText() {
      if (!this.product || !this.product.originalPrice) return ''
      const pct = Math.round((this.product.price / this.product.originalPrice) * 100)
      return (100 - pct) + '% OFF'
    }
  },
  methods: {
    async loadProduct() {
      const id = this.$route.params.id
      if (!id) { this.$message.error('商品ID无效'); this.loading = false; return }
      try {
        const res = await getProductDetail(id)
        this.product = (res && res.data) ? res.data : null
      } catch { this.product = null }
      finally { this.loading = false }
    },
    handleBuy() {
      this.$message.success('已下单：' + this.product.name + ' × ' + this.quantity)
    },
    handleAddCart() {
      this.$message.success('已加入购物车：' + this.product.name + ' × ' + this.quantity)
    }
  },
  created() { this.loadProduct() }
}
</script>

<style scoped>
.product-detail-page { max-width: 1200px; margin: 0 auto; padding: 24px 20px 48px; background: #fff; min-height: 60vh; }
.detail-breadcrumb { margin-bottom: 24px; }
.detail-layout { display: flex; gap: 48px; }
.detail-left { flex-shrink: 0; width: 420px; }
.detail-image { width: 100%; aspect-ratio: 1/1; border-radius: 12px; overflow: hidden; }
.detail-right { flex: 1; display: flex; flex-direction: column; gap: 16px; }
.detail-name { font-size: 24px; font-weight: 700; color: #333; margin: 0; font-family: 'Microsoft YaHei', sans-serif; }
.detail-tags { display: flex; gap: 8px; }
.detail-price { display: flex; align-items: baseline; gap: 10px; padding: 16px; background: #FFF5F5; border-radius: 8px; }
.detail-price__current { font-size: 28px; font-weight: 700; color: #C71526; }
.detail-price__original { font-size: 16px; color: #bbb; text-decoration: line-through; }
.detail-meta { display: flex; flex-direction: column; gap: 10px; padding: 12px 0; border-top: 1px solid #f0f0f0; border-bottom: 1px solid #f0f0f0; }
.detail-meta__row { display: flex; gap: 12px; }
.detail-meta__label { font-size: 14px; color: #999; min-width: 48px; }
.detail-meta__value { font-size: 14px; color: #333; }
.text-danger { color: #C71526; font-weight: 600; }
.detail-quantity { display: flex; align-items: center; gap: 16px; }
.detail-quantity__label { font-size: 14px; color: #999; }
.detail-actions { display: flex; gap: 16px; margin-top: 8px; }
.detail-actions .el-button--danger { background: #C71526; border-color: #C71526; padding: 14px 40px; font-size: 16px; }
.detail-actions .el-button--default { padding: 14px 40px; font-size: 16px; }
@media (max-width: 768px) {
  .detail-layout { flex-direction: column; }
  .detail-left { width: 100%; }
  .detail-name { font-size: 20px; }
}
</style>
```

### 3.3 注册路由

> **文件：** `frontend/src/router/index.js`

```js
// 新增 import
const ProductDetail = () => import('../views/shop/ProductDetail.vue')

// 在 /shop 的 children 中追加
{ path: 'product/:id', name: 'ProductDetail', component: ProductDetail }
```

### 3.4 预览验证

| 序号 | 验证点 | 预期结果 |
|------|--------|---------|
| 1 | ProductController | `GET /api/product/{id}` 返回商品详情或错误 |
| 2 | product.js | 已追加 `getProductDetail`，累计 6 个函数 |
| 3 | ProductDetail.vue | 面包屑、大图、价格（含原价删除线）、库存、数量选择器 |
| 4 | 路由 | 首页点击商品卡片跳转 `#/shop/product/1` 正常 |

---

## 四、评价列表页  `20min`

### 4.1 后端：Review 模块追加全部评价查询

#### ① ReviewMapper.java（追加方法）

> **文件：** `backend/src/main/java/com/huashan/backend/mapper/ReviewMapper.java`

```java
/**
 * 查询全部评价（按时间倒序）
 */
List<Review> findAll();
```

#### ② ReviewMapper.xml（追加 SQL）

> **文件：** `backend/src/main/resources/com/huashan/backend/mapper/ReviewMapper.xml`

```xml
<!-- 查询全部评价（按创建时间倒序） -->
<select id="findAll" resultType="com.huashan.backend.entity.Review">
    select <include refid="Base_Column_List"/> from review
    order by create_time desc
</select>
```

#### ③ ReviewService.java（追加方法）

> **文件：** `backend/src/main/java/com/huashan/backend/service/ReviewService.java`

```java
/**
 * 获取全部评价
 */
List<Review> getAllReviews();
```

#### ④ ReviewServiceImpl.java（追加实现）

> **文件：** `backend/src/main/java/com/huashan/backend/service/impl/ReviewServiceImpl.java`

在已有类中追加：

```java
@Override
public List<Review> getAllReviews() {
    return reviewMapper.findAll();
}
```

#### ⑤ ReviewController.java（追加 list 方法）

> **文件：** `backend/src/main/java/com/huashan/backend/controller/ReviewController.java`

在已有类中追加（不分页，一次性返回全部）：

```java
/**
 * 获取全部评价列表（不分页）
 * GET /api/review/list
 */
@GetMapping("/list")
public Result list() {
    List<Review> list = reviewService.getAllReviews();
    return Result.success(list);
}
```

### 4.2 前端：product.js 追加 + ReviewList.vue 新建

#### 4.2.1 追加 API 函数

> **文件：** `frontend/src/api/product.js`（末尾追加，累计 7 个）

```js
/**
 * 获取评价列表（不分页，一次性获取全部）
 */
export function getReviewList() {
  return request.get('/review/list')
}
```

#### 4.2.2 新建 ReviewList.vue

> **文件：** `frontend/src/views/shop/ReviewList.vue`（新建）

评价卡片样式与首页晒单评价区完全一致（`.review-card`、`.review-grid` 等类名从 HomeView.vue 原样复制）：

```vue
<template>
  <div class="review-list-page">
    <div class="page-header">
      <h1 class="page-header__title">晒单评价</h1>
      <p class="page-header__sub">共 {{ reviewList.length }} 条真实用户评价</p>
    </div>

    <div v-if="loading" class="review-grid">
      <div v-for="n in 8" :key="'rsk-' + n" class="review-card-skeleton">
        <el-skeleton animated>
          <template #template>
            <div style="display:flex;align-items:center;gap:10px;margin-bottom:12px">
              <el-skeleton-item variant="circle" style="width:30px;height:30px" />
              <div>
                <el-skeleton-item variant="text" style="width:40px;height:14px" />
                <el-skeleton-item variant="text" style="width:60px;height:12px;margin-top:4px" />
              </div>
            </div>
            <el-skeleton-item variant="text" style="width:100%" />
            <el-skeleton-item variant="text" style="width:70%" />
            <div style="margin-top:10px">
              <el-skeleton-item variant="image" style="width:100%;height:140px" />
            </div>
          </template>
        </el-skeleton>
      </div>
    </div>

    <el-empty v-else-if="reviewList.length === 0" description="暂无用户评价" />

    <div v-else class="review-grid">
      <div v-for="review in reviewList" :key="review.id" class="review-card">
        <div class="review-card__header">
          <div class="review-card__avatar" :style="{ background: avatarColor(review.username) }">
            {{ (review.username || '?')[0] }}
          </div>
          <div class="review-card__user-info">
            <span class="review-card__user">{{ review.username }}</span>
            <div class="review-card__rating-row">
              <el-rate :model-value="review.rating || 5" disabled show-score text-color="#ff9900" size="small" />
            </div>
            <span class="review-card__date">{{ formatDate(review.createTime) }}</span>
          </div>
        </div>
        <p class="review-card__content">{{ review.content }}</p>
        <div v-if="review.image" class="review-card__img-box">
          <img class="review-card__img" :src="review.image" alt="晒单图片" />
        </div>
        <span v-if="review.location" class="review-card__location">{{ review.location }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { getReviewList } from '../../api/product'

export default {
  name: 'ReviewList',
  data() {
    return {
      reviewList: [],
      loading: false,
      avatarColors: ['#FF8A80','#FFD180','#EA80FC','#82B1FF','#80CBC4','#FF80AB','#B388FF','#8C9EFF']
    }
  },
  methods: {
    async loadReviews() {
      this.loading = true
      try {
        const res = await getReviewList()
        this.reviewList = Array.isArray(res.data) ? res.data : []
      } catch { this.reviewList = [] }
      finally { this.loading = false }
    },
    avatarColor(username) {
      if (!username) return this.avatarColors[0]
      let hash = 0
      for (let i = 0; i < username.length; i++) {
        hash = username.charCodeAt(i) + ((hash << 5) - hash)
      }
      return this.avatarColors[Math.abs(hash) % this.avatarColors.length]
    },
    formatDate(dateStr) {
      if (!dateStr) return ''
      const d = new Date(dateStr)
      if (isNaN(d.getTime())) return dateStr
      const y = d.getFullYear()
      const m = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${y}-${m}-${day}`
    }
  },
  created() { this.loadReviews() }
}
</script>

<style scoped>
.review-list-page { max-width: 1200px; margin: 0 auto; padding: 32px 20px 48px; background: #fff; min-height: 60vh; }
.page-header { text-align: center; margin-bottom: 28px; }
.page-header__title { font-size: 24px; font-weight: 700; color: #333; margin: 0 0 8px; }
.page-header__sub { font-size: 14px; color: #999; margin: 0; }
.review-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.review-card { background: #fff; border: 1px solid #f0f0f0; border-radius: 10px; padding: 16px; transition: box-shadow 0.3s; }
.review-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.06); }
.review-card__header { display: flex; align-items: flex-start; gap: 10px; margin-bottom: 12px; }
.review-card__avatar { width: 30px; height: 30px; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 13px; font-weight: 700; flex-shrink: 0; }
.review-card__user-info { display: flex; flex-direction: column; }
.review-card__user { font-size: 13px; font-weight: 700; color: #333; }
.review-card__rating-row { margin: 2px 0; }
.review-card__date { font-size: 10px; color: #bbb; }
.review-card__content { font-size: 13px; color: #555; line-height: 1.6; margin: 0 0 10px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.review-card__img-box { width: 100%; overflow: hidden; border-radius: 6px; margin-bottom: 4px; }
.review-card__img { width: 100%; aspect-ratio: 4/3; object-fit: cover; display: block; }
.review-card__location { font-size: 11px; color: #999; }
.review-card-skeleton { background: #fff; border: 1px solid #f0f0f0; border-radius: 10px; padding: 16px; }
@media (max-width: 1024px) { .review-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 768px) { .review-grid { grid-template-columns: 1fr; } }
</style>
```

### 4.3 注册路由和导航入口

```js
// router/index.js：新增 import
const ReviewList = () => import('../views/shop/ReviewList.vue')

// children 中追加：
{ path: 'reviews', name: 'ReviewList', component: ReviewList }
```

ShopLayout.vue 的 `headerNavs` 中添加导航入口：

```js
{ name: '晒单评价', link: '/shop/reviews', active: false }
```

### 4.4 预览验证

| 序号 | 验证点 | 预期结果 |
|------|--------|---------|
| 1 | ReviewMapper | 追加 `findAll()`，XML 追加对应 SQL |
| 2 | ReviewServiceImpl | 实现 `getAllReviews()` |
| 3 | ReviewController | 追加 `list()` 方法 |
| 4 | product.js | 追加 `getReviewList`，累计 7 个函数 |
| 5 | ReviewList.vue | `#/shop/reviews` 展示全部评价，无分页 |
| 6 | 样式一致性 | 与首页晒单评价区视觉完全一致 |
## 五、路由完善与登录优化  `25min`
在所有页面功能正常的基础上，增强请求拦截器和路由守卫，保护需要登录的页面。
### 6.1 增强 request.js 响应拦截器
当前 request.js 只处理了 code !== 200 的情况，需要增强以处理 401 未授权：
```
import router from '../router'
request.interceptors.response.use(
response => {
const res = response.data
if (res.code === 401) {
handleUnauthorized()
return Promise.reject(new Error('未登录'))
}
if (res.code !== 200 && res.code !== undefined) {
ElMessage.error(res.message || '操作失败')
return Promise.reject(new Error(res.message || '操作失败'))
}
return res
},
error => {
if (error.response && error.response.status === 401) {
handleUnauthorized()
return Promise.reject(new Error('登录已过期'))
}
ElMessage.error(error.response?.data?.message || '网络错误')
return Promise.reject(error)
}
)
function handleUnauthorized() {
localStorage.removeItem('token')
localStorage.removeItem('username')
ElMessage.error('登录已过期，请重新登录')
router.push('/shop/login')
}
```
### 6.2 添加全局路由守卫
```
router.beforeEach((to, from, next) => {
const token = localStorage.getItem('token')
const authPaths = ['/shop/cart', '/shop/order', '/shop/user']
const requiresAuth = authPaths.some(path => to.path.startsWith(path))
if (requiresAuth && !token) {
next({ path: '/shop/login', query: { redirect: to.fullPath } })
return
}
next()
})
```
#### 为什么用 to.matched.some() 而不是 to.meta？
`to.matched` 是一个数组，包含从根路由到当前路由的所有匹配记录。使用 `some()` 是因为 `meta` 可以定义在父路由上，子路由会继承父路由的 `meta`。如果只检查 `to.meta`，就会漏掉父路由上定义的 `meta`。
### 6.3 登录后跳转回原页面
```
// Login.vue 登录成功后
async onLogin() {
const res = await login(this.form)
localStorage.setItem('token', res.data.token)
localStorage.setItem('username', res.data.username)
this.$message.success('登录成功')
const redirect = this.$route.query.redirect
this.$router.push(redirect || '/shop')
}
```
### 6.4 预览验证
#### 预览步骤
- 修改 request.js，增强响应拦截器
- 修改 router/index.js，添加 beforeEach 守卫
- 修改 Login.vue，添加 redirect 处理
- 清除 localStorage 中的 token（模拟未登录）
- 访问需要登录的页面，确认自动跳转到登录页
- 登录后确认跳转回原页面
## 六、项目部署与演示  `20min`
所有功能开发完成后，进行项目部署和最终演示。
### 7.1 后端部署
- 确保 MySQL 服务已启动，数据库已初始化
- 确认 application.yml 中的数据库连接配置正确
- 使用 Maven 打包：`mvn clean package -DskipTests`
- 运行 jar 包：`java -jar target/backend-0.0.1-SNAPSHOT.jar`
- 确认后端服务在 `http://localhost:8080` 正常运行
### 7.2 前端部署
- 确认 vite.config.js 中的代理配置指向正确后端地址
- 开发模式运行：`npm run dev`
- 生产构建：`npm run build`
- 构建产物在 `dist` 目录，可部署到 Nginx 等静态服务器
### 7.3 最终演示清单
| 功能 | 验证点 | 预期结果 
| 首页布局 | 导航栏、搜索、底部版权 | 所有页面共享统一布局 
| 商品列表 | 分页、分类筛选 | 正常分页和筛选 
| 商品搜索 | 导航栏搜索框输入关键词 | 列表更新为搜索结果 
| 商品评价 | 首页晒单评价区 | 显示最新 8 条评价 
| 商品详情 | 点击商品卡片 | 显示完整商品信息 
| 评价列表 | 导航栏"晒单评价"入口 | 分页显示所有评价 
| 登录优化 | 未登录访问需登录页面 | 自动跳转登录页，登录后返回 
### 7.4 完整功能总结
| 步骤 | 功能 | 涉及文件 
| 步骤一 | 首页结构分析与 Layout 提取 | ShopLayout.vue, router/index.js 
| 步骤二 | 商品评价功能 | ReviewController.java, product.js, Home.vue 
| 步骤三 | 商品搜索功能 | ProductController.java, product.js, Home.vue 
| 步骤四 | 商品详情页 | ProductController.java, product.js, ProductDetail.vue, router/index.js, Home.vue 
| 步骤五 | 评价列表页 | ReviewController.java, product.js, ReviewList.vue, router/index.js, ShopLayout.vue 
| 步骤六 | 路由完善与登录优化 | request.js, router/index.js, Login.vue 
| 步骤七 | 项目部署与演示 | application.yml, vite.config.js 
> > **后续扩展方向**
> 
完成以上 7 个步骤后，还可以继续扩展以下功能：
- **购物车页面**：/shop/cart，展示购物车列表，支持修改数量、删除、结算
- **订单确认页**：/shop/order/:id，选择地址、确认订单信息、提交订单
- **我的订单**：/shop/my-orders，展示历史订单列表
- **用户中心**：/shop/user，展示用户信息，支持修改资料

