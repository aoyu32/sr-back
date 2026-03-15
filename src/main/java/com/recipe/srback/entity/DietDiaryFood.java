package com.recipe.srback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 打卡食物记录表
 */
@Data
@TableName("diet_diary_food")
public class DietDiaryFood {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long mealId;
    
    private String foodImage;
    
    private String foodName;
    
    private Integer calories;
    
    private BigDecimal protein;
    
    private BigDecimal carbs;
    
    private BigDecimal fat;
    
    private String amount;
    
    private BigDecimal aiConfidence;
    
    private Integer isManual;
    
    private Integer sortOrder;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
