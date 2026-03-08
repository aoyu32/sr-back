package com.recipe.srback.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 食谱详情VO
 */
@Data
public class RecipeDetailVO {
    private String id;
    
    private String name;
    
    private String category;
    
    private String image;
    
    private Integer likes;
    
    private Integer collections;
    
    private Integer views;
    
    private NutritionVO nutrition;
    
    private String description;
    
    private List<IngredientVO> ingredients;
    
    private Boolean isLiked;
    
    private Boolean isCollected;
    
    private List<String> tags;
    
    @Data
    public static class NutritionVO {
        private Integer calories;
        private String protein;
        private String carbs;
        private String fat;
    }
    
    @Data
    public static class IngredientVO {
        private String name;
        private String amount;
    }
}
