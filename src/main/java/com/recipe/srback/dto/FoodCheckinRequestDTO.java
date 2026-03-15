package com.recipe.srback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 食物打卡请求DTO
 */
@Data
public class FoodCheckinRequestDTO {
    
    /**
     * 食物图片URL
     */
    @JsonProperty("image_url")
    private String imageUrl;
    
    /**
     * 餐次类型：breakfast/lunch/dinner
     */
    @JsonProperty("meal_type")
    private String mealType;
    
    /**
     * 固定值："打卡"
     */
    private String input;
}
