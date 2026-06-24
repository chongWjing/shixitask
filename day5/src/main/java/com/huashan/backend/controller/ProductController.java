package com.huashan.backend.controller;

import com.huashan.backend.entity.Product;
import com.huashan.backend.service.ProductService;
import com.huashan.backend.util.PageResult;
import com.huashan.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String category){
        //声明分页结果的变量
        PageResult<Product> pageResult;
        //判定是否传入了分类的参数
        if(category!=null && !category.isEmpty()){
            pageResult = productService.getProductListByCategory(page,size,category);
        }else{
            pageResult = productService.getProductList(page,size);
        }
        return Result.success(pageResult);
    }
}
