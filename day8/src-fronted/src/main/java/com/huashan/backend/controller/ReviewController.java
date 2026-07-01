package com.huashan.backend.controller;

import com.huashan.backend.entity.Review;
import com.huashan.backend.service.ReviewService;
import com.huashan.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/latest")
    public Result latest(@RequestParam(defaultValue = "8") int limit){
        List<Review>  list=reviewService.getLatestReviews(limit);
        return Result.success(list);
    }


    @GetMapping("/list")
    public Result List(){
        List<Review> list=reviewService.getAllReviews();
        return Result.success(list);
    }

}
