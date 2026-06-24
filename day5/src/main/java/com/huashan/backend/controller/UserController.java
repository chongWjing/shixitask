package com.huashan.backend.controller;


import com.huashan.backend.service.UserService;
import com.huashan.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    //用户登录功能
    @PostMapping("/login")
    public Result login(@RequestBody Map<String,String> params){

        return Result.success(userService.login(params.get("username"),params.get("password")));

    }


    //注册接口
    @PostMapping("/register")
    public Result register(@RequestBody Map<String,String> params){
        userService.register(params.get("username"),params.get("password"),params.get("phone"));
        return Result.success("注册成功");
    }



}
