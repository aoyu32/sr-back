package com.recipe.srback.service;

import com.recipe.srback.dto.RecipeCategorySaveAdminDTO;
import com.recipe.srback.vo.RecipeCategoryAdminVO;

import java.util.List;

/**
 * 后台食谱分类管理服务
 */
public interface RecipeCategoryAdminService {

    List<RecipeCategoryAdminVO> listCategories(String keyword);

    RecipeCategoryAdminVO getCategoryById(Long categoryId);

    Long createCategory(RecipeCategorySaveAdminDTO dto);

    void updateCategory(Long categoryId, RecipeCategorySaveAdminDTO dto);

    void deleteCategory(Long categoryId);
}
