package com.recipe.srback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 食谱收藏实体类
 */
@Data
@TableName("recipe_collection")
public class RecipeCollection {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long recipeId;
    
    private Long userId;
    
    private LocalDateTime createdAt;
}
