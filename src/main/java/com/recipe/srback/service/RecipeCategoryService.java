package com.recipe.srback.service;

import com.recipe.srback.vo.RecipeCategoryVO;

import java.util.List;

/**
 * 食谱分类服务接口
 */
public interface RecipeCategoryService {
    
    /**
     * 查询所有分类
     */
    List<RecipeCategoryVO> getAllCategories();
}
