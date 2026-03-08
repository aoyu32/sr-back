package com.recipe.srback.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 更新健康档案DTO
 */
@Data
public class UpdateHealthProfileDTO {
    private BigDecimal height;
    
    private BigDecimal weight;
    
    private Integer age;
    
    private Integer gender;
    
    private String activityLevel;
    
    private String bloodPressure;
    
    private BigDecimal bloodSugar;
}
