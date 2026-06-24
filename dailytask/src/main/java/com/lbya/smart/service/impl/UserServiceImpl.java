package com.lbya.smart.service.impl;

import com.lbya.smart.interceptor.TokenStore;
import com.lbya.smart.model.User;
import com.lbya.smart.service.UserService;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户服务实现 - 内存存储（MVP）
 * 用户数据和 Token 均存储在 HashMap 中，重启后丢失
 */
@Service
public class UserServiceImpl implements UserService {

    /** 用户存储：key = username */
    private final Map<String, User> userStore = new ConcurrentHashMap<>();

    /** 用户ID自增 */
    private final AtomicLong idGenerator = new AtomicLong(1);

    public UserServiceImpl() {
        // 初始化测试用户（密码：123456 → MD5）
        User testUser = new User();
        testUser.setId(idGenerator.getAndIncrement());
        testUser.setUsername("test");
        testUser.setPassword(md5("123456"));
        testUser.setPhone("13912345678");
        testUser.setRole(0);
        userStore.put("test", testUser);
    }

    @Override
    public Map<String, Object> login(String username, String password) {
        User user = userStore.get(username);
        if (user == null) {
            throw new IllegalArgumentException("用户名不存在");
        }
        if (!user.getPassword().equals(md5(password))) {
            throw new IllegalArgumentException("密码错误");
        }

        // 生成 Token
        String token = UUID.randomUUID().toString().replace("-", "");
        TokenStore.putUser(token, user);

        // 返回 { token, user }
        Map<String, Object> result = new HashMap<>();
        result.put("token", "Bearer " + token);
        result.put("user", toUserInfo(user));
        return result;
    }

    @Override
    public void logout(String token) {
        if (token != null) {
            TokenStore.removeUser(token);
        }
    }

    @Override
    public String register(String username, String password, String phone) {
        if (userStore.containsKey(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }

        User user = new User();
        user.setId(idGenerator.getAndIncrement());
        user.setUsername(username);
        user.setPassword(md5(password));
        user.setPhone(phone);
        user.setRole(0);

        userStore.put(username, user);
        return "注册成功";
    }

    @Override
    public Map<String, Object> getUserInfo(String token) {
        User user = TokenStore.getUser(token);
        if (user == null) {
            throw new IllegalArgumentException("未登录或登录已过期");
        }
        return toUserInfo(user);
    }

    @Override
    public void updateProfile(Long userId, String phone) {
        User user = findUserById(userId);
        if (user != null) {
            user.setPhone(phone);
        }
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (!user.getPassword().equals(md5(oldPassword))) {
            throw new IllegalArgumentException("原密码错误");
        }
        user.setPassword(md5(newPassword));
    }

    // ========== 工具方法 ==========

    private User findUserById(Long id) {
        return userStore.values().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private Map<String, Object> toUserInfo(User user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("role", user.getRole());
        info.put("phone", user.getPhone());
        return info;
    }

    /**
     * MD5 加密（和前端注册时加密方式一致）
     */
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes("UTF-8"));
            BigInteger number = new BigInteger(1, digest);
            StringBuilder hexString = new StringBuilder(number.toString(16));
            while (hexString.length() < 32) {
                hexString.insert(0, "0");
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 加密失败", e);
        }
    }
}
