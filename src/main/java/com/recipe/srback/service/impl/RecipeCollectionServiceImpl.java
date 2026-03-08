package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recipe.srback.entity.Recipe;
import com.recipe.srback.entity.RecipeCollection;
import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.mapper.RecipeCollectionMapper;
import com.recipe.srback.mapper.RecipeMapper;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.service.RecipeCollectionService;
import com.recipe.srback.vo.RecipeListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 食谱收藏服务实现类
 */
@Slf4j
@Service
public class RecipeCollectionServiceImpl implements RecipeCollectionService {
    
    @Autowired
    private RecipeCollectionMapper collectionMapper;
    
    @Autowired
    private RecipeMapper recipeMapper;
    
    @Override
    public List<RecipeListVO> getMyCollections(Long userId) {
        // 查询用户的收藏记录
        LambdaQueryWrapper<RecipeCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecipeCollection::getUserId, userId);
        wrapper.orderByDesc(RecipeCollection::getCreatedAt);
        
        List<RecipeCollection> collections = collectionMapper.selectList(wrapper);
        
        // 获取食谱详情
        return collections.stream()
                .map(collection -> {
                    Recipe recipe = recipeMapper.selectById(collection.getRecipeId());
                    if (recipe != null && recipe.getStatus() == 1) {
                        return convertToListVO(recipe, true, false);
                    }
                    return null;
                })
                .filter(vo -> vo != null)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelCollection(Long userId, Long recipeId) {
        LambdaQueryWrapper<RecipeCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecipeCollection::getUserId, userId);
        wrapper.eq(RecipeCollection::getRecipeId, recipeId);
        
        RecipeCollection collection = collectionMapper.selectOne(wrapper);
        if (collection == null) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        
        collectionMapper.deleteById(collection.getId());
        
        // 更新食谱收藏数
        Recipe recipe = recipeMapper.selectById(recipeId);
        if (recipe != null && recipe.getCollectionsCount() > 0) {
            recipe.setCollectionsCount(recipe.getCollectionsCount() - 1);
            recipeMapper.updateById(recipe);
        }
    }
    
    /**
     * 转换为列表VO
     */
    private RecipeListVO convertToListVO(Recipe recipe, boolean isCollected, boolean isLiked) {
        RecipeListVO vo = new RecipeListVO();
        vo.setId(recipe.getId());
        vo.setName(recipe.getName());
        vo.setCover(recipe.getImage());
        vo.setCategoryId(recipe.getCategoryId().toString());
        vo.setKcal(recipe.getCalories());
        
        // 处理健康目标标签
        if (recipe.getGoalTags() != null && !recipe.getGoalTags().isEmpty()) {
            List<String> tags = Arrays.asList(recipe.getGoalTags().split(","));
            vo.setTags(tags);
        }
        
        return vo;
    }
}
