package com.lbya.smart.controller;

import com.lbya.smart.common.Result;
import com.lbya.smart.model.User;
import com.lbya.smart.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理控制器
 * 对接前端商城登录/注册/退出等接口
 *
 * 后端返回格式：{ code: 200, message: "操作成功", data: { token, user } }
 * 前端 axios 拦截器自动处理 code != 200 的情况
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户登录
     * POST /user/login
     * 前端传：{ "username": "test", "password": "123456" }
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return Result.error("用户名和密码不能为空");
        }

        try {
            Map<String, Object> data = userService.login(username, password);
            return Result.success(data);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户注册
     * POST /user/register
     * 前端传：{ "username": "zhangsan", "password": "123456", "phone": "13912345678" }
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String phone = body.get("phone");

        if (username == null || username.isBlank()) {
            return Result.error("用户名不能为空");
        }
        if (password == null || password.length() < 6) {
            return Result.error("密码不能少于6位");
        }
        if (phone == null || !phone.matches("^1[3-9]\\d{9}$")) {
            return Result.error("手机号格式不正确");
        }

        try {
            String msg = userService.register(username, password, phone);
            return Result.success(msg);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 退出登录
     * POST /user/logout
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        String token = extractToken(request);
        userService.logout(token);
        return Result.success("已退出登录");
    }

    /**
     * 获取当前登录用户信息
     * GET /user/info
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return Result.error(401, "未登录");
        }
        try {
            Map<String, Object> info = userService.getUserInfo(token);
            return Result.success(info);
        } catch (IllegalArgumentException e) {
            return Result.error(401, e.getMessage());
        }
    }

    /**
     * 更新用户资料（手机号）
     * PUT /user/profile
     */
    @PutMapping("/profile")
    public Result<String> updateProfile(HttpServletRequest request,
                                        @RequestBody Map<String, String> body) {
        User user = (User) request.getAttribute("currentUser");
        if (user == null) {
            return Result.error(401, "未登录");
        }
        String phone = body.get("phone");
        userService.updateProfile(user.getId(), phone);
        return Result.success("更新成功");
    }

    /**
     * 修改密码
     * PUT /user/password
     */
    @PutMapping("/password")
    public Result<String> changePassword(HttpServletRequest request,
                                         @RequestBody Map<String, String> body) {
        User user = (User) request.getAttribute("currentUser");
        if (user == null) {
            return Result.error(401, "未登录");
        }
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        try {
            userService.changePassword(user.getId(), oldPassword, newPassword);
            return Result.success("密码修改成功");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /** 从 Authorization 头中提取 token */
    private String extractToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return null;
    }
}
