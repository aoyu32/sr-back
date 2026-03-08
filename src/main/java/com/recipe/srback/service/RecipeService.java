package com.recipe.srback.service;

import com.recipe.srback.vo.RecipeDetailVO;
import com.recipe.srback.vo.RecipeListVO;
import com.recipe.srback.vo.RecipeRankingVO;

import java.util.List;

/**
 * 食谱服务接口
 */
public interface RecipeService {
    
    /**
     * 查询所有食谱
     */
    List<RecipeListVO> getAllRecipes();
    
    /**
     * 根据分类查询食谱
     */
    List<RecipeListVO> getRecipesByCategory(String categoryCode);
    
    /**
     * 根据健康目标标签查询食谱
     */
    List<RecipeListVO> getRecipesByGoalTag(String goalTag);
    
    /**
     * 根据分类和健康目标标签查询食谱
     */
    List<RecipeListVO> getRecipesByCategoryAndGoalTag(String categoryCode, String goalTag);
    
    /**
     * 根据ID查询食谱详情
     */
    RecipeDetailVO getRecipeDetailById(Long id, Long userId);
    
    /**
     * 根据关键词搜索食谱（搜索名称和分类）
     */
    List<RecipeListVO> searchRecipes(String keyword);
    
    /**
     * 点赞食谱
     */
    void likeRecipe(Long recipeId, Long userId);
    
    /**
     * 取消点赞食谱
     */
    void unlikeRecipe(Long recipeId, Long userId);
    
    /**
     * 收藏食谱
     */
    void collectRecipe(Long recipeId, Long userId);
    
    /**
     * 取消收藏食谱
     */
    void uncollectRecipe(Long recipeId, Long userId);
    
    /**
     * 查询食谱排行榜（按点赞数排序，取前10）
     */
    List<RecipeRankingVO> getRecipeRankings();
}
