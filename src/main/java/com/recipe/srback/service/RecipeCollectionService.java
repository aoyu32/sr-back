package com.recipe.srback.service;

import com.recipe.srback.vo.RecipeListVO;

import java.util.List;

/**
 * 食谱收藏服务接口
 */
public interface RecipeCollectionService {
    /**
     * 获取我的收藏列表
     */
    List<RecipeListVO> getMyCollections(Long userId);
    
    /**
     * 取消收藏
     */
    void cancelCollection(Long userId, Long recipeId);
}
