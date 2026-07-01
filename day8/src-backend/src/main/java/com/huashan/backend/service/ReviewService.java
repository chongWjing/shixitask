package com.huashan.backend.service;

import com.huashan.backend.entity.Review;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author admin
* @description 针对表【review(商品评价表)】的数据库操作Service
* @createDate 2026-06-22 11:05:29
*/
public interface ReviewService extends IService<Review> {
    List< Review>  getLatestReviews(int limit);
    List< Review> getAllReviews();
}
