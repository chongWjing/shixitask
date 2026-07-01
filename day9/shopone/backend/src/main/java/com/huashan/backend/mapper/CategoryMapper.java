package com.huashan.backend.mapper;

import com.huashan.backend.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author admin
* @description 针对表【category(商品分类表)】的数据库操作Mapper
* @createDate 2026-06-22 11:05:29
* @Entity com.huashan.backend.entity.Category
*/
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    //查询所有的启动的分类
    List< Category> findAllEnabled();
}




