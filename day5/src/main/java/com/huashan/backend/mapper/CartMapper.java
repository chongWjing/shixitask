package com.huashan.backend.mapper;

import com.huashan.backend.entity.Cart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author admin
* @description 针对表【cart(购物车表)】的数据库操作Mapper
* @createDate 2026-06-22 16:20:59
* @Entity com.huashan.backend.entity.Cart
*/
@Mapper
public interface CartMapper extends BaseMapper<Cart> {

}




