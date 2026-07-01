package com.huashan.backend.mapper;

import com.huashan.backend.entity.Review;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author admin
* @description 针对表【review(商品评价表)】的数据库操作Mapper
* @createDate 2026-06-22 11:05:29
* @Entity com.huashan.backend.entity.Review
*/
@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
    //获取最新的评论
    List<Review> findLatest(@Param("limit")  int limit);

    List<Review> findAll();

}




