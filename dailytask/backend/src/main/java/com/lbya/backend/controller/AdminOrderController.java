package com.lbya.backend.controller;

import com.lbya.backend.entity.Orders;
import com.lbya.backend.util.PageResult;
import com.lbya.backend.service.OrdersService;
import com.lbya.backend.service.UserService;
import com.lbya.backend.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/order")
public class AdminOrderController {

    @Autowired private OrdersService ordersService;
    @Autowired private UserService userService;

    private Result<?> checkAdmin(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return Result.error(401, "未登录");
        if (!userService.checkAdmin(userId)) return Result.error(403, "无权限");
        return null;
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) Integer status,
                          HttpServletRequest request) {
        Result<?> auth = checkAdmin(request);
        if (auth != null) return auth;
        PageResult<Orders> pageResult = status != null
                ? ordersService.getAllOrdersByStatus(page, size, status)
                : ordersService.getAllOrders(page, size);
        return Result.success("查询成功", pageResult);
    }

    @PutMapping("/status")
    public Result<?> updateStatus(@RequestBody Map<String, Integer> params, HttpServletRequest request) {
        Result<?> auth = checkAdmin(request);
        if (auth != null) return auth;
        Integer id = params.get("id");
        Integer status = params.get("status");
        if (id == null) return Result.error(400, "订单ID不能为空");
        if (status == null) return Result.error(400, "订单状态不能为空");
        try {
            ordersService.updateOrderStatus(id, status);
            return Result.success("订单状态修改成功", "订单状态修改成功");
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }
}
