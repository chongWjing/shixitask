package com.lbya.backend.mapper;

import com.lbya.backend.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author admin
* @description 针对表【product(商品表)】的数据库操作Mapper
* @createDate 2026-06-22 11:05:29
* @Entity com.lbya.backend.entity.Product
*/
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    //分页查上架的商品
    List<Product> findByPage(int offset, int size);

    //查询总记录数
    long count();

    //根据关键词查询商品
    List<Product>  searchByKeyword(String keyword);

    //根据分类查询商品
    List<Product> findByCategoryPage(@Param("offset") int offset, @Param("size") int size,@Param("category") String category);

    //统计指定分类下的商品数量
    long countByCategory(@Param("category") String  category);

    //全文搜索的商品功能
    List<Product> searchByKeywordFullText(@Param("keyword") String keyword);

    //根据关键词分页查询商品
    List<Product> searchByKeywordPage(@Param("offset") int offset, @Param("size") int size,@Param("keyword") String keyword);

    //统计关键词搜索匹配的商品总数
    long countByKyword(@Param("keyword") String keyword);

    // Admin methods
    List<Product> findAllByPage(@Param("offset") int offset, @Param("size") int size);
    long countAll();

    // 库存与销量
    int decreaseStock(@Param("id") Integer id, @Param("quantity") Integer quantity);
    int increaseSales(@Param("id") Integer id, @Param("quantity") Integer quantity);
}




