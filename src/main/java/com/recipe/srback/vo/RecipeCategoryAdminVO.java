package com.recipe.srback.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台食谱分类信息
 */
@Data
public class RecipeCategoryAdminVO {

    private Long id;

    private String name;

    private String code;

    private Integer sortOrder;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
