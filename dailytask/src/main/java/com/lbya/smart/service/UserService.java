package com.lbya.smart.service;

import com.lbya.smart.model.User;

import java.util.Map;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户登录
     * @return { token: "Bearer xxx", user: { id, username, role, phone } }
     */
    Map<String, Object> login(String username, String password);

    /** 退出登录 */
    void logout(String token);

    /**
     * 用户注册
     * @return 注册成功消息
     */
    String register(String username, String password, String phone);

    /**
     * 根据 token 获取用户信息
     * @return { id, username, role, phone }
     */
    Map<String, Object> getUserInfo(String token);

    /** 更新手机号 */
    void updateProfile(Long userId, String phone);

    /** 修改密码 */
    void changePassword(Long userId, String oldPassword, String newPassword);
}
