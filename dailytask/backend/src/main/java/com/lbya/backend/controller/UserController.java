package com.lbya.backend.controller;

import com.lbya.backend.entity.User;
import com.lbya.backend.service.UserService;
import com.lbya.backend.util.JwtUtil;
import com.lbya.backend.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map<String,String> params){
        return Result.success(userService.login(params.get("username"),params.get("password")));
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody Map<String,String> params){
        userService.register(params.get("username"),params.get("password"),params.get("phone"));
        return Result.success("注册成功");
    }

    /**
     * 获取当前用户信息
     * GET /api/user/info
     */
    @GetMapping("/info")
    public Result getUserInfo(HttpServletRequest request) {
        // 从拦截器设置的request属性中获取userId
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error(400, "用户不存在");
        }
        // 不返回密码
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 退出登录
     * POST /api/user/logout
     * JWT无状态，服务端不需要操作，前端清除token即可
     */
    @PostMapping("/logout")
    public Result logout() {
        return Result.success("退出登录成功");
    }
}
