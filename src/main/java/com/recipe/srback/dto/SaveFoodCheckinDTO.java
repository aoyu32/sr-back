package com.recipe.srback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 保存食物打卡DTO
 */
@Data
public class SaveFoodCheckinDTO {
    
    /**
     * 食物图片URL
     */
    @JsonProperty("food_image")
    private String foodImage;
    
    /**
     * 餐次类型：breakfast/lunch/dinner
     */
    @JsonProperty("meal_type")
    private String mealType;
    
    /**
     * 食物名称
     */
    @JsonProperty("food_name")
    private String foodName;
    
    /**
     * 热量（千卡）
     */
    private Integer calories;
    
    /**
     * 蛋白质（克）
     */
    private Double protein;
    
    /**
     * 碳水化合物（克）
     */
    private Double carbs;
    
    /**
     * 脂肪（克）
     */
    private Double fat;
    
    /**
     * 份量描述
     */
    private String amount;
    
    /**
     * 识别置信度
     */
    private Double confidence;
}
