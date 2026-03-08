package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recipe.srback.entity.Recipe;
import com.recipe.srback.entity.RecipeCollection;
import com.recipe.srback.mapper.RecipeCollectionMapper;
import com.recipe.srback.mapper.RecipeMapper;
import com.recipe.srback.service.MyContentService;
import com.recipe.srback.vo.MyContentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 我的内容统计服务实现类
 */
@Slf4j
@Service
public class MyContentServiceImpl implements MyContentService {
    
    @Autowired
    private RecipeCollectionMapper collectionMapper;
    
    @Autowired
    private RecipeMapper recipeMapper;
    
    @Override
    public MyContentVO getMyContentStats(Long userId) {
        MyContentVO vo = new MyContentVO();
        
        // 统计收藏数
        LambdaQueryWrapper<RecipeCollection> collectionWrapper = new LambdaQueryWrapper<>();
        collectionWrapper.eq(RecipeCollection::getUserId, userId);
        Long collectionsCount = collectionMapper.selectCount(collectionWrapper);
        vo.setCollections(collectionsCount.intValue());
        
        // 统计我的食谱数
        LambdaQueryWrapper<Recipe> recipeWrapper = new LambdaQueryWrapper<>();
        recipeWrapper.eq(Recipe::getCreatorId, userId);
        Long recipesCount = recipeMapper.selectCount(recipeWrapper);
        vo.setRecipes(recipesCount.intValue());
        
        return vo;
    }
}
