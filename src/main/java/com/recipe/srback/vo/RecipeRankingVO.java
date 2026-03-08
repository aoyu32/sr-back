package com.recipe.srback.vo;

import lombok.Data;

/**
 * 食谱排行榜VO
 */
@Data
public class RecipeRankingVO {
    private Integer rank;
    
    private Long id;
    
    private String image;
    
    private String name;
    
    private Integer likes;
    
    private Integer views;
}
