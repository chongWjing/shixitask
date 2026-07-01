package com.huashan.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huashan.backend.entity.Orders;
import com.huashan.backend.entity.Product;
import com.huashan.backend.mapper.ProductMapper;
import com.huashan.backend.service.OrdersService;
import com.huashan.backend.mapper.OrdersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
* @author admin
* @description 针对表【orders(订单表)】的数据库操作Service实现
* @createDate 2026-06-22 11:05:29
*/
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
    implements OrdersService{
    @Autowired
    private OrdersMapper orderMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 创建订单 — 8 步流程
     */
    public Orders createOrder(Integer userId, Integer productId, Integer quantity,
                             String receiverName, String receiverPhone, String receiverAddress) {
        // 1. 查询商品，验证存在性
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        // 2. 验证库存
        if (product.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        // 3. 扣减库存
        //productMapper.decreaseStock(productId, quantity);

        // 4. 增加销量
       // productMapper.increaseSales(productId, quantity);

        // 5. 生成唯一订单号
        String orderNo = "ORD" + System.currentTimeMillis();

        // 6. 计算应付总额
        BigDecimal totalPrice = product.getPrice().multiply(new BigDecimal(quantity));

        // 7. 构建订单对象，快照商品信息
        Orders order = new Orders();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setProductId(productId);
        order.setProductName(product.getName());       // 快照
        order.setProductImage(product.getImage());     // 快照
        order.setProductPrice(product.getPrice());     // 快照
        order.setQuantity(quantity);
        order.setTotalPrice(totalPrice);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setReceiverAddress(receiverAddress);
        order.setStatus(0);  // 初始状态：待付款

        // 8. 插入数据库
        orderMapper.insert(order);
        return order;
    }

    /** 获取用户的所有订单 */
    public List<Orders> getMyOrders(Integer userId) {
        return orderMapper.findByUserId(userId);
    }

    /** 更新订单状态 */
    public void updateOrderStatus(Integer id, Integer status) {
        orderMapper.updateStatus(id, status);
    }

    /**
     * 用户取消订单
     * 允许：待付款(0)、已付款(1)
     * 拒绝：已发货(2)、已完成(3)、已取消(4)
     */
    public void cancelOrder(Integer userId, Integer orderId) {
        Orders order = orderMapper.findById(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        if (!order.getUserId().equals(userId)) throw new RuntimeException("无权操作此订单");
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            throw new RuntimeException("当前订单状态不允许取消");
        }
        orderMapper.updateStatus(orderId, 4);
    }

    /**
     * 用户付款（模拟）
     * 只允许：待付款(0) → 已付款(1)
     */
    public void payOrder(Integer userId, Integer orderId) {
        Orders order = orderMapper.findById(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        if (!order.getUserId().equals(userId)) throw new RuntimeException("无权操作此订单");
        if (order.getStatus() != 0) throw new RuntimeException("当前订单状态不允许付款");
        orderMapper.updateStatus(orderId, 1);
    }

    /**
     * 用户确认收货
     * 只允许：已发货(2) → 已完成(3)
     */
    public void confirmOrder(Integer userId, Integer orderId) {
        Orders order = orderMapper.findById(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        if (!order.getUserId().equals(userId)) throw new RuntimeException("无权操作此订单");
        if (order.getStatus() != 2) throw new RuntimeException("当前订单状态不允许确认收货");
        orderMapper.updateStatus(orderId, 3);
    }
}




