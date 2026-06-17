package com.lbya.smart.common;

import lombok.Data;

/**
 * 统一响应结果类
 * 所有接口统一返回 { code, message, data }
 */
@Data
public class Result<T> {

    /** 状态码：200=成功，500=失败 */
    private int code;
    /** 提示信息 */
    private String message;
    /** 数据 */
    private T data;

    /**
     * 成功响应
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 失败响应
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    /**
     * 失败响应（自定义状态码）
     */
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
