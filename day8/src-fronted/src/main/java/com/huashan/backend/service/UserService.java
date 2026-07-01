package com.huashan.backend.service;

import com.huashan.backend.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
* @author admin
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2026-06-22 11:05:29
*/
public interface UserService extends IService<User> {
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 包含token和用户信息的 Map
     */
    Map<String,Object> login(String username, String password);

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param phone 手机号
     * @return 注册成功的提示信息
     */
    String register(String username, String password,String phone);
}
