package com.lbya.smart.interceptor;

import com.lbya.smart.model.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token 存储（内存版 MVP）
 * Token → User 映射，提供 getUser / putUser / removeUser
 *
 * 注意：此存储在 UserServiceImpl 中也被使用，双方共享同一个 TokenStore
 * 重启后数据丢失（MVP 阶段可接受）
 */
public class TokenStore {

    private static final Map<String, User> STORE = new ConcurrentHashMap<>();

    public static User getUser(String token) {
        return STORE.get(token);
    }

    public static void putUser(String token, User user) {
        STORE.put(token, user);
    }

    public static void removeUser(String token) {
        STORE.remove(token);
    }
}
