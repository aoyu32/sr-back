package com.recipe.srback.vo;

import lombok.Data;

import java.util.List;

/**
 * 后台分页结果
 */
@Data
public class PageAdminVO<T> {

    private Integer pageNum;

    private Integer pageSize;

    private Long total;

    private List<T> records;

    public static <T> PageAdminVO<T> of(Integer pageNum, Integer pageSize, Long total, List<T> records) {
        PageAdminVO<T> page = new PageAdminVO<>();
        page.setPageNum(pageNum);
        page.setPageSize(pageSize);
        page.setTotal(total);
        page.setRecords(records);
        return page;
    }
}
