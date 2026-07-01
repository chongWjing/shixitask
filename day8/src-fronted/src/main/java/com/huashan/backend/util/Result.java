package com.huashan.backend.util;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;
    //返回成功的结果
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    //指定返回信息
    public static <T> Result<T> success(String message, T data){
        Result<T> result = new Result<>();
        result.setCode(200);//响应代码
        result.setMessage(message);//指定的返回信息
        result.setData(data);//返回结果
        return result;
    }


    //返回失败的结果
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    //返回自定义的状态码
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
