package com.huashan.backend.mapper;

import com.huashan.backend.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author admin
* @description 针对表【orders(订单表)】的数据库操作Mapper
* @createDate 2026-06-22 11:05:29
* @Entity com.huashan.backend.entity.Orders
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
}




