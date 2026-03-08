package com.recipe.srback.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 添加健康目标DTO
 */
@Data
public class AddHealthGoalDTO {
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
