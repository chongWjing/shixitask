package com.huashan.backend.controller;

import com.huashan.backend.entity.Orders;
import com.huashan.backend.service.OrdersService;
import com.huashan.backend.util.ParamUtil;
import com.huashan.backend.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrdersController {

    @Autowired
    private OrdersService orderService;

    /**
     * 创建订单
     * POST /api/order/create
     * Body: { productId, quantity, receiverName, receiverPhone, receiverAddress }
     */
    @PostMapping("/create")
    public Result create(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Integer userId = (Integer) request.getAttribute("userId");
        Integer productId = ParamUtil.getInt(params, "productId");
        Integer quantity = ParamUtil.getInt(params, "quantity", 1);
        String receiverName = ParamUtil.getString(params, "receiverName");
        String receiverPhone = ParamUtil.getString(params, "receiverPhone");
        String receiverAddress = ParamUtil.getString(params, "receiverAddress");
        Orders order = orderService.createOrder(userId, productId, quantity,
                receiverName, receiverPhone, receiverAddress);
        return Result.success(order);
    }

    /**
     * 获取我的订单列表
     * GET /api/order/my
     */
    @GetMapping("/my")
    public Result myOrders(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        List<Orders> orders = orderService.getMyOrders(userId);
        return Result.success(orders);
    }

    /**
     * 取消订单
     * PUT /api/order/cancel/{id}
     */
    @PutMapping("/cancel/{id}")
    public Result cancel(HttpServletRequest request, @PathVariable Integer id) {
        Integer userId = (Integer) request.getAttribute("userId");
        orderService.cancelOrder(userId, id);
        return Result.success("取消成功");
    }

    /**
     * 付款（模拟）
     * PUT /api/order/pay/{id}
     */
    @PutMapping("/pay/{id}")
    public Result pay(HttpServletRequest request, @PathVariable Integer id) {
        Integer userId = (Integer) request.getAttribute("userId");
        orderService.payOrder(userId, id);
        return Result.success("付款成功");
    }

    /**
     * 确认收货
     * PUT /api/order/confirm/{id}
     */
    @PutMapping("/confirm/{id}")
    public Result confirm(HttpServletRequest request, @PathVariable Integer id) {
        Integer userId = (Integer) request.getAttribute("userId");
        orderService.confirmOrder(userId, id);
        return Result.success("确认收货成功");
    }

}
