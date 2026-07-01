package com.huashan.backend.mapper;

import com.huashan.backend.entity.Cart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author admin
* @description 针对表【cart(购物车表)】的数据库操作Mapper
* @createDate 2026-06-22 11:05:28
* @Entity com.huashan.backend.entity.Cart
*/
@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    //添加购物车
    int insert(Cart cart);
    //查询购物，根据用户查询
    List<Cart> findByUserId(Integer userId);

    //根据ID查询购物车的项
    Cart findById(Integer id);

    Cart findByUserIdAndProductId(@Param("userId") Integer userId,@Param("productId") Integer productId);

    //更新购物车数量
    int updateQuantity(@Param("id") Integer id,@Param("quantity") Integer  quantity);

    //删除购物车项
    int deleteById(Integer id);

    //清空购物车
    int deleteByUserId(Integer userId);

}




