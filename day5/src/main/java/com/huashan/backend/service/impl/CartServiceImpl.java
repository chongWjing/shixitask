package com.huashan.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huashan.backend.entity.Cart;
import com.huashan.backend.service.CartService;
import com.huashan.backend.mapper.CartMapper;
import org.springframework.stereotype.Service;

/**
* @author admin
* @description 针对表【cart(购物车表)】的数据库操作Service实现
* @createDate 2026-06-22 16:20:58
*/
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart>
    implements CartService{

}




