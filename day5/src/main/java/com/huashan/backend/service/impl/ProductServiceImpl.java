package com.huashan.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huashan.backend.entity.Product;
import com.huashan.backend.service.ProductService;
import com.huashan.backend.mapper.ProductMapper;
import com.huashan.backend.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author admin
* @description 针对表【product(商品表)】的数据库操作Service实现
* @createDate 2026-06-22 16:20:59
*/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService{

    @Autowired
    private ProductMapper productMapper;


    /**
     * 分页查询所有商品
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<Product> getProductList(int page, int size) {
        //计算页码是多少
        int offset=(page-1)*size;
        //查询当前页的商品数据
        List<Product> products=productMapper.findByPage(offset,size);
        //商品的总数
        long total=productMapper.count();
        //封装为PageResult对象并返回
        return new PageResult<>(products,total,page,size);
    }

    /**
     * 根据分类分页查询商品
     * @param page
     * @param size
     * @param category
     * @return
     */
    @Override
    public PageResult<Product> getProductListByCategory(int page, int size, String category) {
        //计算页面的偏移量
        int offset=(page-1)*size;
        //查询指定分类下当前页的商品数据
        List<Product> products=productMapper.findByCategoryPage(offset,size,category);
        //查询指定分类下的商品总数
        long total=productMapper.countByCategory(category);
        //封装为PageResult对象并返回
        return new PageResult<>(products,total,page,size);
    }

    @Override
    public List<Product> searchProduct(String keyword) {
        return productMapper.searchByKeyword(keyword);
    }


    /**
     * 模糊查询
     * @param keyword
     * @param page
     * @param size
     * @return
     */
    @Override
    public PageResult<Product> searchProductPaged(String keyword, int page, int size) {
        // 计算偏移量
        int offset = (page - 1) * size;
        //查询当前页的搜索结果
        List<Product> products = productMapper.searchByKeywordPage(keyword, offset, size);
        //查询搜索结果总数
        long total = productMapper.countByKeyword(keyword);
        //封装为PageResult对象并返回
        return new PageResult<>(products, total, page, size);
    }
}




