package com.huashan.backend.service;

import com.huashan.backend.entity.Cart;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author admin
* @description 针对表【cart(购物车表)】的数据库操作Service
* @createDate 2026-06-22 11:05:28
*/
public interface CartService extends IService<Cart> {

   List<Cart> getCartList(Integer userId);
   Cart getCartItem(Integer id);
   Cart addToCart(Integer userId,Integer productId,Integer quantity);
   void updateQuantity(Integer id,Integer quantity);
   void deleteCartItem(Integer id);
   void clearCart(Integer userId);

}
