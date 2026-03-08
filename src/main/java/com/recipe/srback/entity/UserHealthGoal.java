package com.recipe.srback.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
    
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal targetWeight;
    
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal targetBmi;
    
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal targetMuscle;
    
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal targetBloodSugar;
    
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String targetBloodPressure;
    
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer dailyCalories;
    
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal dailyProtein;
    
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal dailyCarbs;
    
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal dailySodium;
    
    private LocalDate startDate;
    
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate endDate;
    
    private String status;
    
    private String result;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
