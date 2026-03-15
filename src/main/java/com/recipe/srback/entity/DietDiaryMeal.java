package com.recipe.srback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 餐次打卡记录表
 */
@Data
@TableName("diet_diary_meal")
public class DietDiaryMeal {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long diaryId;
    
    private String mealType;
    
    private LocalDateTime checkTime;
    
    private Integer isChecked;
    
    private Integer mealCalories;
    
    private BigDecimal mealProtein;
    
    private BigDecimal mealCarbs;
    
    private BigDecimal mealFat;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
