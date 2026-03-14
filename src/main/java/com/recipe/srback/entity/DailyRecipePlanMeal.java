package com.recipe.srback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 餐次计划表
 */
@Data
@TableName("daily_recipe_plan_meal")
public class DailyRecipePlanMeal {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long planId;
    
    private String mealType;
    
    private String mealName;
    
    private String timeRange;
    
    private Integer mealCalories;
    
    private BigDecimal mealProtein;
    
    private BigDecimal mealCarbs;
    
    private BigDecimal mealFat;
    
    private Integer sortOrder;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
