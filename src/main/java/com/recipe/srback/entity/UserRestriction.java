package com.recipe.srback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户特殊禁忌实体
 */
@Data
@TableName("user_restriction")
public class UserRestriction {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String type;
    
    private String name;
    
    private String description;
    
    private String severity;
    
    private LocalDate addedDate;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
