package com.huashan.backend.util;

import lombok.Data;

//统一响应的封装
@Data
public class Result <T> {//可以根据需要指定具体的数据类型
    private int code;//状态码
    private String message; //返回信息
    private T data;

    //静态的泛型方法，返回成功的结果
    public static <T> Result<T> success(T data){
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    //自定义返回信息的静态方法
    public static <T> Result<T> success(String message,T data){
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }


    //静态的泛型方法，返回失败的结果
    public static <T> Result<T> error(String message){
        Result<T> result = new Result<>();
        result.setCode(500);//表示服务器错误
        result.setMessage(message);
        return result;
    }

    //自定义错误代码的静态方法
    public static <T> Result<T> error(int code,String message){
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

}
