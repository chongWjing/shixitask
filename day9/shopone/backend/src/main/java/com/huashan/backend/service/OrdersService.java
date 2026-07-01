package com.huashan.backend.service;

import com.huashan.backend.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import com.huashan.backend.util.PageResult;

import java.util.List;

/**
* @author admin
* @description 针对表【orders(订单表)】的数据库操作Service
* @createDate 2026-06-22 11:05:29
*/
public interface OrdersService extends IService<Orders> {
    public Orders createOrder(Integer userId, Integer productId, Integer quantity,
                              String receiverName, String receiverPhone, String receiverAddress);
    public List<Orders> getMyOrders(Integer userId);
    public void cancelOrder(Integer userId, Integer orderId);
    public void payOrder(Integer userId, Integer orderId);
    public void confirmOrder(Integer userId, Integer orderId);
    //public PageResult<Orders> getAllOrdersByStatus(int page, int size, Integer status);
}
