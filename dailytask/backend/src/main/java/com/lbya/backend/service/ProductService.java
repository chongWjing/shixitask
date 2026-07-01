package com.lbya.backend.service;

import com.lbya.backend.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lbya.backend.util.PageResult;

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
    List<Product> searchProductFulltext(String keyword);
    PageResult<Product> searchProductPaged(int page,int size,String keyword);
    Product getById(Integer id);

    /** admin: 新增商品 */
    String addProduct(Product product);
    /** admin: 修改商品 */
    String updateProduct(Product product);
    /** admin: 修改商品状态 */
    String updateProductStatus(Integer id, Integer status);
}
