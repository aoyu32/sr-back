package com.recipe.srback.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 创建食谱DTO
 */
@Data
public class CreateRecipeDTO {
    private String name;
    
    private String image;
    
    private String categoryId; // 改为String，接收分类code
    
    private Integer calories;
    
    private BigDecimal protein;
    
    private BigDecimal carbs;
    
    private BigDecimal fat;
    
    private String description;
    
    private List<IngredientDTO> ingredients;
    
    private List<String> goalTags;
    
    @Data
    public static class IngredientDTO {
        private String name;
        private String amount;
    }
}
