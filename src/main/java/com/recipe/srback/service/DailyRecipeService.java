package com.recipe.srback.service;

import com.recipe.srback.vo.DailyRecipeVO;

/**
 * 每日食谱推荐服务接口
 */
public interface DailyRecipeService {
    
    /**
     * 生成每日食谱推荐
     * 
     * @param userId 用户ID
     * @param input 用户输入的生成提示
     * @return 每日食谱推荐结果
     */
    DailyRecipeVO generateDailyRecipe(Long userId, String input);

    /**
     * 获取今日食谱推荐（优先从数据库查询，不存在则自动生成）
     *
     * @param userId 用户ID
     * @return 每日食谱推荐结果
     */
    DailyRecipeVO getTodayRecipe(Long userId);
}
