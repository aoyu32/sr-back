package com.recipe.srback.service;

import com.recipe.srback.dto.CreateRecipeDTO;
import com.recipe.srback.dto.UpdateRecipeDTO;
import com.recipe.srback.vo.RecipeListVO;

import java.util.List;

/**
 * 我的食谱服务接口
 */
public interface MyRecipeService {
    /**
     * 创建我的食谱
     */
    Long createRecipe(Long userId, CreateRecipeDTO dto);
    
    /**
     * 获取我的食谱列表
     */
    List<RecipeListVO> getMyRecipes(Long userId);
    
    /**
     * 更新我的食谱
     */
    void updateRecipe(Long userId, Long recipeId, UpdateRecipeDTO dto);
    
    /**
     * 删除我的食谱
     */
    void deleteRecipe(Long userId, Long recipeId);
}
