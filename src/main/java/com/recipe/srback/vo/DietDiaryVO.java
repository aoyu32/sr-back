package com.recipe.srback.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 饮食日记VO
 */
@Data
public class DietDiaryVO {
    
    /**
     * 日期（YYYY-MM-DD）
     */
    private String date;
    
    /**
     * 星期几
     */
    private String weekday;
    
    /**
     * 总热量
     */
    @JsonProperty("totalCalories")
    private Integer totalCalories;
    
    /**
     * 总蛋白质
     */
    @JsonProperty("totalProtein")
    private Double totalProtein;
    
    /**
     * 总碳水
     */
    @JsonProperty("totalCarbs")
    private Double totalCarbs;
    
    /**
     * 总脂肪
     */
    @JsonProperty("totalFat")
    private Double totalFat;
    
    /**
     * 已打卡餐次列表
     */
    @JsonProperty("checkedMeals")
    private List<String> checkedMeals;
    
    /**
     * 餐次详情
     */
    private Map<String, MealDetailVO> meals;
    
    /**
     * 餐次详情VO
     */
    @Data
    public static class MealDetailVO {
        
        /**
         * 餐次名称
         */
        private String time;
        
        /**
         * 打卡时间
         */
        @JsonProperty("timeRange")
        private String timeRange;
        
        /**
         * 餐次热量
         */
        private Integer calories;
        
        /**
         * 是否已打卡
         */
        private Boolean checked;
        
        /**
         * 食物列表
         */
        private List<FoodDetailVO> foods;
    }
    
    /**
     * 食物详情VO
     */
    @Data
    public static class FoodDetailVO {
        
        /**
         * 食物ID
         */
        private Long id;
        
        /**
         * 食物名称
         */
        private String name;
        
        /**
         * 食物图片
         */
        private String image;
        
        /**
         * 份量
         */
        private String amount;
        
        /**
         * 热量
         */
        private Integer calories;
        
        /**
         * 蛋白质
         */
        private Double protein;
        
        /**
         * 碳水
         */
        private Double carbs;
        
        /**
         * 脂肪
         */
        private Double fat;
    }
}
