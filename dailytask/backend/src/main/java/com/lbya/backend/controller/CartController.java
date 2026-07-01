package com.lbya.backend.controller;

import com.lbya.backend.entity.Cart;
import com.lbya.backend.service.CartService;
import com.lbya.backend.util.ParamUtil;
import com.lbya.backend.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;


    //根据当前用户查询购物车信息
    @GetMapping("/list")
    public Result list(HttpServletRequest request){
        Integer userId=(Integer) request.getAttribute("userId");
        List<Cart> cartList=cartService.getCartList(userId);
        return Result.success(cartList);
    }

    @PostMapping("/add")
    public Result add(HttpServletRequest request, @RequestBody Map<String,Object> params){
        Integer userId=(Integer) request.getAttribute("userId");
        Integer productId=Integer.parseInt(params.get("productId").toString());
        Integer quantity=Integer.parseInt(params.get("quantity").toString());
        Cart cart=cartService.addToCart(userId,productId,quantity);
        return Result.success(cart);
    }

    @PutMapping("/update")
    public Result update(HttpServletRequest request, @RequestBody Map<String,Object> params){
        Integer userId=(Integer) request.getAttribute("userId");
        Integer id= ParamUtil.getInt(params,"id");
        Integer quantity=ParamUtil.getInt(params,"quantity");

        Cart cart=cartService.getCartItem(id);
        if(cart==null || cart.getUserId()!=userId){
            return Result.error("没有权限操作");
        }
        cartService.updateQuantity(id,quantity);
        return Result.success("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(HttpServletRequest request, @PathVariable Integer id){
        Integer userId=(Integer) request.getAttribute("userId");
        Cart cart=cartService.getCartItem(id);
        if(cart==null || cart.getUserId()!=userId){
            return Result.error("没有权限操作");
        }
        cartService.deleteCartItem(id);
        return Result.success("删除成功");
    }


    @DeleteMapping("/clear")
    public Result clear(HttpServletRequest request){
        Integer userId=(Integer) request.getAttribute("userId");
        cartService.clearCart(userId);
        return Result.success("清空成功");
    }
}
