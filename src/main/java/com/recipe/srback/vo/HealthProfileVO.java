package com.recipe.srback.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 健康档案VO
 */
@Data
public class HealthProfileVO {
    private Long userId;
    
    private BigDecimal height;
    
    private BigDecimal weight;
    
    private Integer age;
    
    private Integer gender;
    
    private BigDecimal bmi;
    
    private String bmiStatus;
    
    private String activityLevel;
    
    private String bloodPressure;
    
    private BigDecimal bloodSugar;
}
