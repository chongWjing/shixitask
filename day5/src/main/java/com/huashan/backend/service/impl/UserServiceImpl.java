package com.huashan.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huashan.backend.entity.User;
import com.huashan.backend.service.UserService;
import com.huashan.backend.mapper.UserMapper;
import com.huashan.backend.util.JwtUtil;
import com.huashan.backend.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
* @author admin
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2026-06-22 16:20:59
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return
     */
    @Override
    public Map<String, Object> login(String username, String password) {
        //用户登录
        //根据用户名从数据库中查询用户的信息
        User user=userMapper.findByUsernameUser(username);
        //判定用户是否存在，如果不存在，就抛出一个异常
        if (user==null) throw new RuntimeException("用户不存在！");
        //将输入的密码进行MD5加密
        String md5Password= Md5Util.encrypt(password);
        //从数据库查询的密码与输入的密码是否一样
        if (!md5Password.equals(user.getPassword())) throw new RuntimeException("密码错误！");
        // 判定用户的状态
        if(user.getStatus()==0) throw new RuntimeException("用户被禁用！");
        //如果校验成功，则需要根据用户id和用户名生成token
        String token=JwtUtil.generateToken(user.getId(),user.getUsername());
        //创建一个Map对象，将token返回给前端，用户信息
        Map<String,Object> map=new HashMap<>();
        //添加token
        map.put("token",token);
        //将密码设置为空
        user.setPassword(null);
        //添加用户信息
        map.put("user",user);

        return map;
    }

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param phone 手机号
     * @return 返回注册信息
     */
    @Override
    public String register(String username, String password, String phone) {
        //检查用户名是否已经存在
        User user=userMapper.findByUsernameUser(username);
        if(user!=null) throw new RuntimeException("用户名已存在！");
        //创建用户实体对象
        user=new User();
        //设置用户的基本信息
        user.setUsername(username);
        user.setPassword(Md5Util.encrypt(password));
        user.setPhone(phone);
        user.setRole(0);//设置用户角色为普通用户
        user.setStatus(1);//设置用户状态为正常
        //将用户信息添加到数据库
        userMapper.insert(user);
        return "注册成功";
    }
}




