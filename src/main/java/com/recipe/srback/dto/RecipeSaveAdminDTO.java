package com.recipe.srback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 后台保存食谱参数
 */
@Data
public class RecipeSaveAdminDTO {

    @NotBlank(message = "请输入食谱名称")
    private String name;

    @NotBlank(message = "请上传食谱图片")
    private String image;

    @NotNull(message = "请选择食谱分类")
    private Long categoryId;

    @NotNull(message = "请输入热量")
    private Integer calories;

    private BigDecimal protein;

    private BigDecimal carbs;

    private BigDecimal fat;

    private String description;

    private List<IngredientItem> ingredients;

    private List<String> goalTags;

    @NotNull(message = "请选择上架状态")
    private Integer status;

    @Data
    public static class IngredientItem {
        private String name;
        private String amount;
    }
}
