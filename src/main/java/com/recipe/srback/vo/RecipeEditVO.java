package com.recipe.srback.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 食谱编辑VO
 */
@Data
public class RecipeEditVO {
    private Long id;
    
    private String name;
    
    private String image;
    
    private String categoryId; // 分类code
    
    private Integer calories;
    
    private BigDecimal protein;
    
    private BigDecimal carbs;
    
    private BigDecimal fat;
    
    private String description;
    
    private List<IngredientVO> ingredients;
    
    private List<String> goalTags;
    
    @Data
    public static class IngredientVO {
        private String name;
        private String amount;
    }
}
