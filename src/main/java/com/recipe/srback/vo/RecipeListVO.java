package com.recipe.srback.vo;

import lombok.Data;

import java.util.List;

/**
 * 食谱列表VO
 */
@Data
public class RecipeListVO {
    private Long id;
    
    private String name;
    
    private Integer kcal;
    
    private String categoryId;
    
    private List<String> tags;
    
    private String cover;
}
