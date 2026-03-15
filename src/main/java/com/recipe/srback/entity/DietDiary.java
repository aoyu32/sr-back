package com.recipe.srback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 饮食日记主表
 */
@Data
@TableName("diet_diary")
public class DietDiary {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private LocalDate diaryDate;
    
    private Integer totalCalories;
    
    private BigDecimal totalProtein;
    
    private BigDecimal totalCarbs;
    
    private BigDecimal totalFat;
    
    private Integer mealsCheckedCount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
