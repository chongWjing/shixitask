package com.huashan.backend.util;

import lombok.Data;

import java.util.List;

@Data
public class PageResult <T>{

    //当前页的数据列表
    private List<T> list;
    //数据总条数
    private long total;
    //当前页码
    private int page;
    //每页显示的条数
    private int size;

    /**
     * 构造方法，初始化分页结果
     * @param list
     * @param total
     * @param page
     * @param size
     */
    public PageResult(List<T> list, long total, int page, int size) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.size = size;
    }
}
