package com.recipe.srback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户健康目标实体
 */
@Data
@TableName("user_health_goal")
public class UserHealthGoal {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
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
    
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private String status;
    
    private String result;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
