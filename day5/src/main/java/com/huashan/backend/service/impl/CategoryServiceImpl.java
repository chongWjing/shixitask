package com.huashan.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huashan.backend.entity.Category;
import com.huashan.backend.service.CategoryService;
import com.huashan.backend.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author admin
* @description 针对表【category(商品分类表)】的数据库操作Service实现
* @createDate 2026-06-22 16:20:59
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{


}




