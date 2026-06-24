package com.huashan.backend.service;

import com.huashan.backend.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.huashan.backend.util.PageResult;

import java.util.List;

/**
* @author admin
* @description 针对表【product(商品表)】的数据库操作Service
* @createDate 2026-06-22 16:20:59
*/
public interface ProductService extends IService<Product> {

    //获取所有上架的商品
    PageResult< Product> getProductList(int page, int size);
    //按照分类获取商品
    PageResult<Product> getProductListByCategory(int page, int size, String category);
    //根据关键词搜索商品
    List<Product> searchProduct(String keyword);
    //根据关键词分页搜索商品
    PageResult<Product> searchProductPaged(String keyword, int page, int size);
}
