package com.recipe.srback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日食谱计划主表
 */
@Data
@TableName("daily_recipe_plan")
public class DailyRecipePlan {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private LocalDate planDate;
    
    private String title;
    
    private String description;
    
    private Integer totalCalories;
    
    private BigDecimal totalProtein;
    
    private BigDecimal totalCarbs;
    
    private BigDecimal totalFat;
    
    private String generationType;
    
    private Integer isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
