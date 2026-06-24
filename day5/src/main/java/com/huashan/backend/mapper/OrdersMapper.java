package com.huashan.backend.mapper;

import com.huashan.backend.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author admin
* @description 针对表【orders(订单表)】的数据库操作Mapper
* @createDate 2026-06-22 16:20:59
* @Entity com.huashan.backend.entity.Orders
*/
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

}




