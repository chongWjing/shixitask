package com.lbya.smart.model;

import lombok.Data;

/**
 * 用户实体
 * MVP 阶段使用内存存储，后续可替换为数据库
 */
@Data
public class User {

    private Long id;
    private String username;
    /** MD5 加密后的密码 */
    private String password;
    private String phone;
    /** 0=普通用户 1=管理员 */
    private int role;
}
