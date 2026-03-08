package com.recipe.srback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户健康档案实体
 */
@Data
@TableName("user_health_profile")
public class UserHealthProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private BigDecimal height;
    
    private BigDecimal weight;
    
    private Integer age;
    
    private BigDecimal bmi;
    
    private String bmiStatus;
    
    private String activityLevel;
    
    private String bloodPressure;
    
    private BigDecimal bloodSugar;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
