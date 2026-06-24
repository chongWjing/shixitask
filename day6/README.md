# Day6 - 技术手册：Spring Boot 项目搭建

## 任务目标

编写 Spring Boot 项目搭建教学文档，涵盖后端开发核心知识点。

## 文档内容

**文件**: `技术手册_第6章_SpringBoot项目搭建.md`

## 章节概览

### 6.1 项目初始化
- Spring Boot 项目创建
- 依赖配置（Web、MyBatis-Plus、MySQL、JWT）

### 6.2 统一响应封装
- `Result<T>` 统一返回格式
- `PageResult<T>` 分页返回格式
- 状态码规范（200/400/401/500）

### 6.3 跨域配置
- `WebMvcConfigurer` 实现
- `@CrossOrigin` 注解
- 前后端分离跨域处理

### 6.4 实体类设计
- MyBatis-Plus 注解（`@TableName`、`@TableId`）
- Lombok 简化代码
- 字段映射

### 6.5 Mapper 映射
- `BaseMapper<T>` 接口
- XML 映射文件
- 自定义 SQL 查询

### 6.6 Service 层
- `IService<T>` 接口
- `ServiceImpl<M, T>` 实现
- 业务逻辑封装

### 6.7 Controller 层
- RESTful API 设计
- 参数校验
- 异常处理

### 6.8 认证拦截器
- `HandlerInterceptor` 实现
- JWT Token 验证
- 拦截器注册配置

## 适用场景

本文档可作为 Spring Boot 后端开发入门教程，适用于：
- 新成员入职培训
- 实习生教学指导
- 个人学习参考

## 相关项目

本手册中的代码示例对应 `day5/` 目录下的完整电商后端项目。
