package com.recipe.srback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 后台保存食谱分类参数
 */
@Data
public class RecipeCategorySaveAdminDTO {

    @NotBlank(message = "请输入分类名称")
    private String name;

    @NotBlank(message = "请输入分类编码")
    private String code;

    @NotNull(message = "请输入排序值")
    private Integer sortOrder;
}
