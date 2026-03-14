package com.recipe.srback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 餐次食谱项表
 */
@Data
@TableName("daily_recipe_plan_meal_item")
public class DailyRecipePlanMealItem {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long mealPlanId;
    
    private Long recipeId;
    
    private String itemName;
    
    private String itemImage;
    
    private String amount;
    
    private Integer calories;
    
    private BigDecimal protein;
    
    private BigDecimal carbs;
    
    private BigDecimal fat;
    
    private String category;
    
    private Integer sortOrder;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
