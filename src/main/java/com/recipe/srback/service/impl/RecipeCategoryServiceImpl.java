package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recipe.srback.entity.RecipeCategory;
import com.recipe.srback.mapper.RecipeCategoryMapper;
import com.recipe.srback.service.RecipeCategoryService;
import com.recipe.srback.vo.RecipeCategoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 食谱分类服务实现类
 */
@Service
public class RecipeCategoryServiceImpl implements RecipeCategoryService {
    
    @Autowired
    private RecipeCategoryMapper recipeCategoryMapper;
    
    @Override
    public List<RecipeCategoryVO> getAllCategories() {
        LambdaQueryWrapper<RecipeCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(RecipeCategory::getSortOrder);
        
        List<RecipeCategory> categories = recipeCategoryMapper.selectList(wrapper);
        
        return categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    /**
     * 转换为VO
     */
    private RecipeCategoryVO convertToVO(RecipeCategory category) {
        RecipeCategoryVO vo = new RecipeCategoryVO();
        vo.setId(category.getCode());
        vo.setName(category.getName());
        return vo;
    }
}
