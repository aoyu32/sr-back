package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recipe.srback.dto.RecipeCategorySaveAdminDTO;
import com.recipe.srback.entity.Recipe;
import com.recipe.srback.entity.RecipeCategory;
import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.mapper.RecipeCategoryMapper;
import com.recipe.srback.mapper.RecipeMapper;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.service.RecipeCategoryAdminService;
import com.recipe.srback.vo.RecipeCategoryAdminVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 后台食谱分类管理服务实现
 */
@Service
@RequiredArgsConstructor
public class RecipeCategoryAdminServiceImpl implements RecipeCategoryAdminService {

    private final RecipeCategoryMapper recipeCategoryMapper;
    private final RecipeMapper recipeMapper;

    @Override
    public List<RecipeCategoryAdminVO> listCategories(String keyword) {
        LambdaQueryWrapper<RecipeCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(RecipeCategory::getSortOrder).orderByDesc(RecipeCategory::getCreatedAt);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(RecipeCategory::getName, keyword).or().like(RecipeCategory::getCode, keyword));
        }
        return recipeCategoryMapper.selectList(wrapper).stream().map(this::toRecipeCategoryAdminVO).toList();
    }

    @Override
    public RecipeCategoryAdminVO getCategoryById(Long categoryId) {
        RecipeCategory category = recipeCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        return toRecipeCategoryAdminVO(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCategory(RecipeCategorySaveAdminDTO dto) {
        checkCodeExists(dto.getCode(), null);

        RecipeCategory category = new RecipeCategory();
        category.setName(dto.getName());
        category.setCode(dto.getCode());
        category.setSortOrder(dto.getSortOrder());
        recipeCategoryMapper.insert(category);
        return category.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Long categoryId, RecipeCategorySaveAdminDTO dto) {
        RecipeCategory category = recipeCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }

        checkCodeExists(dto.getCode(), categoryId);
        category.setName(dto.getName());
        category.setCode(dto.getCode());
        category.setSortOrder(dto.getSortOrder());
        recipeCategoryMapper.updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long categoryId) {
        RecipeCategory category = recipeCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }

        LambdaQueryWrapper<Recipe> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Recipe::getCategoryId, categoryId);
        if (recipeMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("该分类下存在食谱，暂时不能删除");
        }

        recipeCategoryMapper.deleteById(categoryId);
    }

    private void checkCodeExists(String code, Long excludeId) {
        LambdaQueryWrapper<RecipeCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecipeCategory::getCode, code);
        if (excludeId != null) {
            wrapper.ne(RecipeCategory::getId, excludeId);
        }
        if (recipeCategoryMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCodeEnum.BAD_REQUEST.getCode(), "分类编码已存在");
        }
    }

    private RecipeCategoryAdminVO toRecipeCategoryAdminVO(RecipeCategory category) {
        RecipeCategoryAdminVO vo = new RecipeCategoryAdminVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }
}
