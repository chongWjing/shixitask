package com.lbya.backend.controller;

import com.lbya.backend.util.PageResult;
import com.lbya.backend.entity.Product;
import com.lbya.backend.mapper.ProductMapper;
import com.lbya.backend.service.ProductService;
import com.lbya.backend.service.UserService;
import com.lbya.backend.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/product")
public class AdminProductController {

    @Autowired private ProductService productService;
    @Autowired private UserService userService;
    @Autowired private ProductMapper productMapper;

    private Result<?> checkAdmin(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        if (userId == null) return Result.error(401, "未登录");
        if (!userService.checkAdmin(userId)) return Result.error(403, "无权限");
        return null;
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "10") int size,
                          HttpServletRequest request) {
        Result<?> auth = checkAdmin(request);
        if (auth != null) return auth;
        int offset = (page - 1) * size;
        List<Product> list = productMapper.findAllByPage(offset, size);
        long total = productMapper.countAll();
        return Result.success("查询成功", new PageResult<>(list, total, page, size));
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Product product, HttpServletRequest request) {
        Result<?> auth = checkAdmin(request);
        if (auth != null) return auth;
        if (product.getName() == null || product.getName().trim().isEmpty())
            return Result.error(400, "商品名称不能为空");
        if (product.getPrice() == null) return Result.error(400, "商品价格不能为空");
        return Result.success(productService.addProduct(product), productService.addProduct(product));
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody Product product, HttpServletRequest request) {
        Result<?> auth = checkAdmin(request);
        if (auth != null) return auth;
        if (product.getId() == null) return Result.error(400, "商品ID不能为空");
        return Result.success(productService.updateProduct(product), productService.updateProduct(product));
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id, HttpServletRequest request) {
        Result<?> auth = checkAdmin(request);
        if (auth != null) return auth;
        return Result.success("删除商品成功", productService.updateProductStatus(id, 0));
    }

    @PutMapping("/status")
    public Result<?> updateStatus(@RequestBody Map<String, Integer> params, HttpServletRequest request) {
        Result<?> auth = checkAdmin(request);
        if (auth != null) return auth;
        Integer id = params.get("id");
        Integer status = params.get("status");
        if (id == null) return Result.error(400, "商品ID不能为空");
        if (status == null) return Result.error(400, "状态不能为空");
        return Result.success("状态修改成功", productService.updateProductStatus(id, status));
    }
}
