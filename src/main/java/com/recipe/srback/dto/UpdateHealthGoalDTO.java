package com.recipe.srback.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 更新健康目标DTO
 */
@Data
public class UpdateHealthGoalDTO {
    private String goalType;
    
    private BigDecimal targetWeight;
    
    private BigDecimal targetBmi;
    
    private BigDecimal targetMuscle;
    
    private BigDecimal targetBloodSugar;
    
    private String targetBloodPressure;
    
    private Integer dailyCalories;
    
    private BigDecimal dailyProtein;
    
    private BigDecimal dailyCarbs;
    
    private BigDecimal dailySodium;
    
    private String endDate;
}
