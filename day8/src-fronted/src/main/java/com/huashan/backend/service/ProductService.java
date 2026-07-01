package com.huashan.backend.service;

import com.huashan.backend.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.huashan.backend.util.PageResult;

import java.util.List;

/**
* @author admin
* @description 针对表【product(商品表)】的数据库操作Service
* @createDate 2026-06-22 11:05:29
*/
public interface ProductService extends IService<Product> {
    public PageResult<Product> getProductList(int page,int size);
    public PageResult<Product> getProductListByCategory(int page,int size,String category);
    List< Product> searchProduct(String keyword);
    PageResult<Product> searchProductPaged(int page,int size,String keyword);
    Product getById(Integer id);
}
