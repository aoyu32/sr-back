package com.recipe.srback.dto;

import lombok.Data;

/**
 * 食谱分析请求DTO
 */
@Data
public class RecipeAnalysisRequestDTO {
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 用户输入
     */
    private String input;
}
