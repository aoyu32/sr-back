package com.recipe.srback.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 食物打卡响应VO
 */
@Data
public class FoodCheckinVO {
    
    /**
     * 食物名称（限制10字以内）
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
     * 份量描述（如：1份、200g、1碗等）
     */
    private String amount;
    
    /**
     * 识别置信度（0.00-1.00）
     */
    private Double confidence;
}
