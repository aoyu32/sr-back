package com.recipe.srback.dto;

import lombok.Data;

/**
 * 添加食谱到餐食DTO
 */
@Data
public class AddRecipeToMealDTO {
    
    /**
     * 食谱ID
     */
    private Long recipeId;
    
    /**
     * 餐次类型：breakfast/lunch/dinner
     */
    private String mealType;
}
