package com.huashan.backend.mapper;

import com.huashan.backend.entity.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author admin
* @description 针对表【product(商品表)】的数据库操作Mapper
* @createDate 2026-06-22 16:20:59
* @Entity com.huashan.backend.entity.Product
*/
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 分页查询所有上架的商品
     * @param offset 起始位置
     * @param size 每页数量
     * @return 商品列表
     */
    List<Product> findByPage(int offset, int size);

    /**
     * 统计所有上架商品的数量
     * @return 商品总数
     */
    long count();

    /**
     * 根据关键字搜索商品
     * @param keyword 关键字
     * @return
     */
    List<Product> searchByKeyword(String keyword);

    /**
     * 根据分类分页查询商品
     * @param offset 起始位置
     * @param size 每页数量
     * @param category  分类
     * @return 商品列表
     */
    List<Product> findByCategoryPage(@Param("offset") int offset,
                                     @Param("size") int size,
                                     @Param("category") String category);

    /**
     * 根据分类统计商品数量
     * @param category  分类
     * @return 商品数量
     */
    long countByCategory(@Param("category") String  category);


    /**
     * 模糊查询 全文搜索
     * @param keyword
     * @return
     */
    List< Product> searchallByKeywordFulltext(@Param("keyword") String keyword);

    /**
     * 分页模糊查询
     * @param keyword 关键字
     * @param offset 起始位置
     * @param size 每页数量
     * @return 商品列表
     */
    List< Product> searchByKeywordPage(@Param("keyword") String keyword,
                                       @Param("offset") int offset,
                                       @Param("size") int size);
    /**
     * 根据关键字统计商品数量
     * @param keyword 关键字
     * @return 商品数量
     */
    long countByKeyword(@Param("keyword") String keyword);
}




