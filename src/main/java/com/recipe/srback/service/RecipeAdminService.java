package com.recipe.srback.service;

import com.recipe.srback.dto.RecipeSaveAdminDTO;
import com.recipe.srback.vo.PageAdminVO;
import com.recipe.srback.vo.RecipeAdminVO;

/**
 * 后台食谱管理服务
 */
public interface RecipeAdminService {

    PageAdminVO<RecipeAdminVO> pageRecipes(Integer pageNum, Integer pageSize, String keyword, Integer status, Long categoryId);

    RecipeAdminVO getRecipeById(Long recipeId);

    Long createRecipe(RecipeSaveAdminDTO dto);

    void updateRecipe(Long recipeId, RecipeSaveAdminDTO dto);

    void deleteRecipe(Long recipeId);
}
