package com.recipe.srback.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 每日食谱推荐响应VO
 */
@Data
public class DailyRecipeVO {
    
    private String type;
    
    private String description;
    
    @JsonProperty("total_calories")
    private Integer totalCalories;
    
    @JsonProperty("total_protein")
    private Double totalProtein;
    
    @JsonProperty("total_carbs")
    private Double totalCarbs;
    
    @JsonProperty("total_fat")
    private Double totalFat;
    
    private Map<String, MealVO> meals;
    
    @Data
    public static class MealVO {
        @JsonProperty("time_range")
        private String timeRange;
        
        @JsonProperty("meal_calories")
        private Integer mealCalories;
        
        @JsonProperty("meal_protein")
        private Double mealProtein;
        
        @JsonProperty("meal_carbs")
        private Double mealCarbs;
        
        @JsonProperty("meal_fat")
        private Double mealFat;
        
        private List<RecipeItemVO> recipes;
    }
    
    @Data
    public static class RecipeItemVO {
        @JsonProperty("item_id")
        private Long itemId;
        
        @JsonProperty("recipe_id")
        private Long recipeId;
        
        @JsonProperty("recipe_name")
        private String recipeName;
        
        private String amount;
        
        private String image;
        
        private String category;
        
        private Integer calories;
        
        private Double protein;
        
        private Double carbs;
        
        private Double fat;
    }
}
