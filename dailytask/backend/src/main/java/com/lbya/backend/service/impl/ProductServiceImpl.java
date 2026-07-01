package com.lbya.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lbya.backend.entity.Product;
import com.lbya.backend.service.ProductService;
import com.lbya.backend.mapper.ProductMapper;
import com.lbya.backend.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author admin
* @description 针对表【product(商品表)】的数据库操作Service实现
* @createDate 2026-06-22 11:05:29
*/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService{

    @Autowired
    private ProductMapper productMapper;

    @Override
    public PageResult<Product> getProductList(int page, int size) {
        //查询数据的偏移量
        int offset = (page - 1) * size;
        //调用mapper查询当前页的商品数据
        List<Product> products = productMapper.findByPage(offset, size);
        //查看商品的总数
        long total = productMapper.count();
        //封装为PageResult对象并返回
        return new PageResult<>(products, total, page, size);
    }

    //分类分页查询商品
    @Override
    public PageResult<Product> getProductListByCategory(int page, int size, String category) {
        //计算偏移量
        int offset=(page-1)*size;
        //指定分类下的商品数据
        List<Product> products=productMapper.findByCategoryPage(offset,size,category);
        long total=productMapper.countByCategory(category);
        return new PageResult<>(products,total,page,size);

    }

    @Override
    public List<Product> searchProduct(String keyword) {
        return productMapper.searchByKeyword(keyword);
    }

    @Override
    public List<Product> searchProductFulltext(String keyword) {
        // 优先使用全文搜索，无结果时回退到LIKE模糊查询
        List<Product> results = productMapper.searchByKeywordFullText(keyword);
        if (results.isEmpty()) {
            results = productMapper.searchByKeyword(keyword);
        }
        return results;
    }

    @Override
    public PageResult<Product> searchProductPaged(int page, int size, String keyword) {
        //计算偏移量
        int offset=(page-1)*size;
        List< Product> products=productMapper.searchByKeywordPage(offset,size,keyword);
        long total=productMapper.countByKyword(keyword);
        return new PageResult<>(products,total,page,size);
    }

    @Override
    public Product getById(Integer id) {
        return productMapper.selectById(id);
    }

    @Override
    public String addProduct(Product product) {
        product.setStatus(1);
        product.setSales(0);
        productMapper.insert(product);
        return "新增商品成功";
    }

    @Override
    public String updateProduct(Product product) {
        productMapper.updateById(product);
        return "修改商品成功";
    }

    @Override
    public String updateProductStatus(Integer id, Integer status) {
        Product product = productMapper.selectById(id);
        if (product == null) return "商品不存在";
        product.setStatus(status);
        productMapper.updateById(product);
        return status == 1 ? "商品上架成功" : "商品下架成功";
    }
}




