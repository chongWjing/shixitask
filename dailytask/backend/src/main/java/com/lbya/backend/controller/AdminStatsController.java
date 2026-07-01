package com.lbya.backend.controller;

import com.lbya.backend.mapper.OrdersMapper;
import com.lbya.backend.mapper.ProductMapper;
import com.lbya.backend.mapper.UserMapper;
import com.lbya.backend.service.UserService;
import com.lbya.backend.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    @Autowired private ProductMapper productMapper;
    @Autowired private OrdersMapper orderMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private UserService userService;

    private Result<?> checkAdmin(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return Result.error(401, "未登录");
        if (!userService.checkAdmin(userId)) return Result.error(403, "无权限");
        return null;
    }

    @GetMapping("/overview")
    public Result<?> overview(HttpServletRequest request) {
        Result<?> auth = checkAdmin(request);
        if (auth != null) return auth;
        Map<String, Object> data = new HashMap<>();
        data.put("productCount", productMapper.countAll());
        data.put("todayOrderCount", orderMapper.countToday());
        data.put("totalSales", orderMapper.sumPaidTotalPrice());
        data.put("userCount", userMapper.selectCount(null));
        return Result.success(data);
    }

    @GetMapping("/orderTrend")
    public Result<?> orderTrend(HttpServletRequest request) {
        Result<?> auth = checkAdmin(request);
        if (auth != null) return auth;
        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            String dateStr = today.minusDays(i).format(fmt);
            Map<String, Object> item = new HashMap<>();
            item.put("date", dateStr);
            item.put("count", orderMapper.countByDate(dateStr));
            result.add(item);
        }
        return Result.success(result);
    }

    @GetMapping("/categorySales")
    public Result<?> categorySales(HttpServletRequest request) {
        Result<?> auth = checkAdmin(request);
        if (auth != null) return auth;
        return Result.success(orderMapper.selectCategorySales());
    }
}
