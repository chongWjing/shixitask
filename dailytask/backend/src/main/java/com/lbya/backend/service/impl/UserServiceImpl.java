package com.lbya.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lbya.backend.entity.User;
import com.lbya.backend.service.UserService;
import com.lbya.backend.mapper.UserMapper;
import com.lbya.backend.util.JwtUtil;
import com.lbya.backend.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
* @author admin
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2026-06-22 11:05:29
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录的业务逻辑实现
     * @param username 用户名
     * @param password 密码
     * @return
     */
    @Override
    public Map<String, Object> login(String username, String password) {
        //根据用户名从数据库中查询信息
        User user = userMapper.findByUsername( username);
        //判断用户是否存在
        if (user == null) throw new RuntimeException("用户不存在");
        //将用户的密码进行MD5加密
        String md5Password = Md5Util.encrypt(password);
        if(!md5Password.equals(user.getPassword())){
            throw new RuntimeException("密码错误");
        }
        //判定用户的状态
        if(user.getStatus()==0) throw new RuntimeException("用户被禁用");
        //使用用户的ID和用户名生成token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());
        //创建一个Map对象，存储返回给前端的数据
        Map<String, Object> result = new HashMap<>();
        //将生成的token返回给前端
        result.put("token", token);
        //把密码设置为空
        user.setPassword(null);
        result.put("user", user);
        return result;
    }

    /**
     * 用户注册的业务逻辑实现
     * @param username 用户名
     * @param password 密码
     * @param phone 手机号
     * @return
     */
    @Override
    public String register(String username, String password, String phone) {
        //1 查询是否已存在该用户
        if(userMapper.findByUsername(username) != null)
            throw new RuntimeException("用户已存在");
        //2 创建一个新的用户实体
        User user = new User();
        //设置用户名
        user.setUsername(username);
        //设置密码，先对明文密码进行MD5加密，后进行存储
        user.setPassword(Md5Util.encrypt(password));
        //设置手机号
        user.setPhone(phone);
        //设置角色为0
        user.setRole(0);
        //设置状态为1
        user.setStatus(1);
        //将用户信息添加到数据库
        userMapper.insert(user);
        //返回注册成功的提示
        return "注册成功";
    }

    @Override
    public User getUserById(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public boolean checkAdmin(Integer userId) {
        if (userId == null) return false;
        User user = userMapper.selectById(userId);
        return user != null && user.getRole() != null && user.getRole() == 1;
    }
}




