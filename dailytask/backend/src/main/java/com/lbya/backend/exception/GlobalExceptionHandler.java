package com.lbya.backend.exception;

import com.lbya.backend.util.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
// 全局异常处理器类，集中处理系统中所有未捕获的异常
public class GlobalExceptionHandler {
    // 异常处理注解：指定捕获 RuntimeException 及其子类异常
    // 当Controller抛出运行时异常时，会由该方法处理
    @ExceptionHandler(RuntimeException.class)
    // 方法返回统一响应结果 Result<Void>，表示不携带业务数据
    // 参数e：被捕获的运行时异常对象，包含异常信息
    public Result<Void> handleRuntimeException(RuntimeException e) {
        // 调用Result.error()返回错误响应，使用异常自带的错误信息
        // 例如：业务校验失败抛出的异常，其message会返回给前端
        return Result.error(e.getMessage());
    }

    // 异常处理注解：指定捕获所有 Exception 类型异常
    // 作为兜底处理器，处理未被前面更具体异常处理器捕获的异常
    // 注意：Exception是RuntimeException的父类，此方法优先级较低
    @ExceptionHandler(Exception.class)
    // 方法返回统一响应结果 Result<Void>
    // 参数e：被捕获的异常对象
    public Result<Void> handleException(Exception e) {
        // 返回通用服务器错误提示，不暴露具体异常细节
        // 避免将内部异常信息泄露给前端，防止安全隐患
        return Result.error("服务器内部错误");
    }
}