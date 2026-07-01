package com.lbya.backend.mapper;

import com.lbya.backend.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author admin
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2026-06-22 11:05:29
* @Entity com.lbya.backend.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {
    //根据用户名来查询下用户
    User findByUsername(String username);
}




