package com.lbya.backend.mapper;

import com.lbya.backend.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author admin
* @description 针对表【orders(订单表)】的数据库操作Mapper
* @createDate 2026-06-22 11:05:29
* @Entity com.lbya.backend.entity.Orders
*/
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
    //添加订单
    int insertOrders(Orders orders);
    //查询订单
    Orders findById(Integer id);
    //查收用户订单
    List<Orders> findByUserId(Integer userId);
    //更新订单
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    // Admin methods
    List<Orders> findByPage(@Param("offset") int offset, @Param("size") int size);
    long countAll();
    List<Orders> findByPageWithStatus(@Param("offset") int offset, @Param("size") int size, @Param("status") Integer status);
    long countByStatus(@Param("status") Integer status);
    long countToday();
    java.math.BigDecimal sumPaidTotalPrice();
    long countByDate(@Param("date") String date);
    java.util.List<java.util.Map<String, Object>> selectCategorySales();
}




