package com.huashan.backend.controller;

import com.huashan.backend.entity.Product;
import com.huashan.backend.service.ProductService;
import com.huashan.backend.util.PageResult;
import com.huashan.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    //商品列表
    @RequestMapping("/list")
    public Result list(@RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String category){
        //声明分页变量
        PageResult<Product> pageResult;
        if(category!=null && !category.isEmpty()){
            //传入了分类
            pageResult = productService.getProductListByCategory(page,size,category);
        }else{
            pageResult = productService.getProductList(page,size);
        }
        //统一封装结果
        return Result.success(pageResult);
    }


    @GetMapping("/search")
    public Result search(@RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam String keyword){
        if(page==1 && size>100){
            List<Product> list=productService.searchProduct( keyword);
            return Result.success(list);
        }
        PageResult<Product> pageResult=productService.searchProductPaged(page,size,keyword);
        return Result.success(pageResult);
    }

    //根据ID查询
    @GetMapping("/{id}")
    public Result detail(@PathVariable Integer id){
        Product product=productService.getById(id);
        if (product==null || product.getStatus()!=1){
            return Result.error("商品不存在");
        }
        return Result.success(product);
    }
}
