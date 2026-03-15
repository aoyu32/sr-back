package com.recipe.srback.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 食谱分析服务接口
 */
public interface RecipeAnalysisService {
    
    /**
     * 分析食谱（流式返回）
     * 
     * @param imageUrl 图片URL
     * @param input 用户输入
     * @return SSE发射器
     */
    SseEmitter analyzeRecipeStream(String imageUrl, String input);
}
