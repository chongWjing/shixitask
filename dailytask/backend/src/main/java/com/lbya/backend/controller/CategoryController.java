package com.lbya.backend.controller;

import com.lbya.backend.entity.Category;
import com.lbya.backend.service.CategoryService;
import com.lbya.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    @GetMapping("/list")
    public Result list(){
        List<Category>  list= categoryService.getAllCategorys();
        return Result.success(list);
    }
}
