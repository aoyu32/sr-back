package com.recipe.srback.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台食谱信息
 */
@Data
public class RecipeAdminVO {

    private Long id;

    private String name;

    private String image;

    private Long categoryId;

    private String categoryName;

    private Integer calories;

    private BigDecimal protein;

    private BigDecimal carbs;

    private BigDecimal fat;

    private String description;

    private List<IngredientItemVO> ingredients;

    private List<String> goalTags;

    private Integer likesCount;

    private Integer collectionsCount;

    private Integer viewsCount;

    private Long creatorId;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Data
    public static class IngredientItemVO {
        private String name;
        private String amount;
    }
}
