package com.recipe.srback.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 今日打卡记录VO
 */
@Data
public class TodayCheckinVO {
    
    /**
     * 餐次列表
     */
    private List<MealCheckinVO> meals;
    
    /**
     * 餐次打卡VO
     */
    @Data
    public static class MealCheckinVO {
        
        /**
         * 餐次类型：breakfast/lunch/dinner
         */
        @JsonProperty("meal_type")
        private String mealType;
        
        /**
         * 餐次标签（早餐/午餐/晚餐）
         */
        private String label;
        
        /**
         * 是否已打卡
         */
        private Boolean checked;
        
        /**
         * 餐次总热量
         */
        private Integer calories;
        
        /**
         * 食物列表
         */
        private List<FoodItemVO> foods;
    }
    
    /**
     * 食物项VO
     */
    @Data
    public static class FoodItemVO {
        
        /**
         * 食物记录ID
         */
        private Long id;
        
        /**
         * 食物图片URL
         */
        @JsonProperty("food_image")
        private String foodImage;
        
        /**
         * 食物名称
         */
        @JsonProperty("food_name")
        private String foodName;
        
        /**
         * 热量
         */
        private Integer calories;
        
        /**
         * 蛋白质
         */
        private Double protein;
        
        /**
         * 碳水化合物
         */
        private Double carbs;
        
        /**
         * 脂肪
         */
        private Double fat;
        
        /**
         * 份量
         */
        private String amount;
        
        /**
         * 识别置信度
         */
        private Double confidence;
    }
}
