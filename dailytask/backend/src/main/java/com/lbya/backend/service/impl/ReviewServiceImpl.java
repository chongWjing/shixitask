package com.lbya.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lbya.backend.entity.Review;
import com.lbya.backend.service.ReviewService;
import com.lbya.backend.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author admin
* @description 针对表【review(商品评价表)】的数据库操作Service实现
* @createDate 2026-06-22 11:05:29
*/
@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review>
    implements ReviewService{

    @Autowired
    private ReviewMapper reviewMapper;


    @Override
    public List<Review> getLatestReviews(int limit) {
        return reviewMapper.findLatest(limit);
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewMapper.findAll();
    }
}




