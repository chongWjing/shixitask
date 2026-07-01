package com.huashan.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huashan.backend.entity.Orders;
import com.huashan.backend.service.OrdersService;
import com.huashan.backend.mapper.OrdersMapper;
import org.springframework.stereotype.Service;

/**
* @author admin
* @description 针对表【orders(订单表)】的数据库操作Service实现
* @createDate 2026-06-22 11:05:29
*/
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
    implements OrdersService{

}




