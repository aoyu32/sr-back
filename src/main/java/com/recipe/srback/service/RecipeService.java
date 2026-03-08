package com.recipe.srback.service;

import com.recipe.srback.vo.RecipeDetailVO;
import com.recipe.srback.vo.RecipeListVO;

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
    RecipeDetailVO getRecipeDetailById(Long id);
}
