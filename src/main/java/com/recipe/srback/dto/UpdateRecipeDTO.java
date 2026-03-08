package com.recipe.srback.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 更新食谱DTO
 */
@Data
public class UpdateRecipeDTO {
    private String name;
    
    private String image;
    
    private String categoryId; // 改为String，接收分类code
    
    private Integer calories;
    
    private BigDecimal protein;
    
    private BigDecimal carbs;
    
    private BigDecimal fat;
    
    private String description;
    
    private List<CreateRecipeDTO.IngredientDTO> ingredients;
    
    private List<String> goalTags;
}
