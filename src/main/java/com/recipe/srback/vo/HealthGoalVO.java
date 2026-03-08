package com.recipe.srback.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 健康目标VO
 */
@Data
public class HealthGoalVO {
    private Long id;
    
    private String goalType;
    
    private String target;
    
    private BigDecimal targetWeight;
    
    private BigDecimal targetBMI;
    
    private BigDecimal targetMuscle;
    
    private BigDecimal targetBloodSugar;
    
    private String targetBloodPressure;
    
    private Integer dailyCalories;
    
    private BigDecimal dailyProtein;
    
    private BigDecimal dailyCarbs;
    
    private BigDecimal dailySodium;
    
    private String startDate;
    
    private String endDate;
    
    private String status;
    
    private String result;
}
