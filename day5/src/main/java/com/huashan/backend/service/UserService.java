package com.huashan.backend.service;

import com.huashan.backend.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
* @author admin
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2026-06-22 16:20:59
*/
public interface UserService extends IService<User> {

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return  返回包含token和用户信息的map
     */
    Map<String,Object> login(String username,String password);


    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param phone 手机号
     * @return 返回注册结果
     */
    String register(String username,String password,String phone);
}
