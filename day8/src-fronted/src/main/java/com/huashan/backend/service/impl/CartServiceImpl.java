package com.huashan.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huashan.backend.entity.Cart;
import com.huashan.backend.entity.Product;
import com.huashan.backend.mapper.ProductMapper;
import com.huashan.backend.service.CartService;
import com.huashan.backend.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author admin
* @description 针对表【cart(购物车表)】的数据库操作Service实现
* @createDate 2026-06-22 11:05:28
*/
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart>
    implements CartService{

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;


    @Override
    public List<Cart> getCartList(Integer userId) {
        return cartMapper.findByUserId(userId);
    }

    @Override
    public Cart getCartItem(Integer id) {
        return cartMapper.findById(id);
    }

    @Override
    public Cart addToCart(Integer userId, Integer productId, Integer quantity) {
        //1 校验添加的商品是否存在
        Product product=productMapper.selectById(productId);
        if(product==null){
            throw new RuntimeException("商品不存在");
        }
        //2 校验商品是否上架
        if(product.getStatus()!=1){
            throw new RuntimeException("商品已下架");
        }
        //3 校验数量
        if(quantity==null || quantity<=0){
            quantity=1;
        }
        //4 检查购物车中是否已经存在该商品
        Cart existCart=cartMapper.findByUserIdAndProductId(userId,productId);
        if(existCart!=null){
            int newQuantity=existCart.getQuantity()+quantity;
            cartMapper.updateQuantity(existCart.getId(),newQuantity);
            existCart.setQuantity(newQuantity);
            return existCart;
        }else{
            Cart cart=new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cartMapper.insert(cart);
            return cart;
        }

    }

    @Override
    public void updateQuantity(Integer id, Integer quantity) {
        if(quantity==null || quantity<=0){
            throw new RuntimeException("数量不能小于1");
        }
        cartMapper.updateQuantity(id,quantity);
    }

    @Override
    public void deleteCartItem(Integer id) {
        cartMapper.deleteById(id);
    }

    @Override
    public void clearCart(Integer userId) {
        cartMapper.deleteByUserId(userId);
    }
}




