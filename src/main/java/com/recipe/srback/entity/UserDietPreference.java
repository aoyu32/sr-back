package com.recipe.srback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户饮食偏好实体
 */
@Data
@TableName("user_diet_preference")
public class UserDietPreference {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String preferenceType;
    
    private String foodName;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
