package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe.srback.dto.RecipeSaveAdminDTO;
import com.recipe.srback.entity.Recipe;
import com.recipe.srback.entity.RecipeCategory;
import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.mapper.RecipeCategoryMapper;
import com.recipe.srback.mapper.RecipeMapper;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.service.RecipeAdminService;
import com.recipe.srback.vo.PageAdminVO;
import com.recipe.srback.vo.RecipeAdminVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 后台食谱管理服务实现
 */
@Service
@RequiredArgsConstructor
public class RecipeAdminServiceImpl implements RecipeAdminService {

    private final RecipeMapper recipeMapper;
    private final RecipeCategoryMapper recipeCategoryMapper;
    private final ObjectMapper objectMapper;

    @Override
    public PageAdminVO<RecipeAdminVO> pageRecipes(Integer pageNum, Integer pageSize, String keyword, Integer status, Long categoryId) {
        Page<Recipe> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Recipe> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Recipe::getCreatedAt);

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Recipe::getName, keyword).or().like(Recipe::getDescription, keyword));
        }
        if (status != null) {
            wrapper.eq(Recipe::getStatus, status);
        }
        if (categoryId != null) {
            wrapper.eq(Recipe::getCategoryId, categoryId);
        }

        Page<Recipe> recipePage = recipeMapper.selectPage(page, wrapper);
        Map<Long, RecipeCategory> categoryMap = buildCategoryMap(recipePage.getRecords());
        List<RecipeAdminVO> records = recipePage.getRecords().stream()
                .map(recipe -> toRecipeAdminVO(recipe, categoryMap.get(recipe.getCategoryId())))
                .toList();
        return PageAdminVO.of(pageNum, pageSize, recipePage.getTotal(), records);
    }

    @Override
    public RecipeAdminVO getRecipeById(Long recipeId) {
        Recipe recipe = getRecipeEntity(recipeId);
        RecipeCategory category = recipeCategoryMapper.selectById(recipe.getCategoryId());
        return toRecipeAdminVO(recipe, category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRecipe(RecipeSaveAdminDTO dto) {
        checkCategoryExists(dto.getCategoryId());

        Recipe recipe = new Recipe();
        fillRecipe(recipe, dto);
        recipe.setLikesCount(0);
        recipe.setCollectionsCount(0);
        recipe.setViewsCount(0);
        recipe.setCreatorId(null);
        recipeMapper.insert(recipe);
        return recipe.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRecipe(Long recipeId, RecipeSaveAdminDTO dto) {
        Recipe recipe = getRecipeEntity(recipeId);
        checkCategoryExists(dto.getCategoryId());
        fillRecipe(recipe, dto);
        recipeMapper.updateById(recipe);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRecipe(Long recipeId) {
        getRecipeEntity(recipeId);
        recipeMapper.deleteById(recipeId);
    }

    private Recipe getRecipeEntity(Long recipeId) {
        Recipe recipe = recipeMapper.selectById(recipeId);
        if (recipe == null) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        return recipe;
    }

    private void checkCategoryExists(Long categoryId) {
        if (recipeCategoryMapper.selectById(categoryId) == null) {
            throw new BusinessException(ResultCodeEnum.PARAM_ERROR.getCode(), "食谱分类不存在");
        }
    }

    private void fillRecipe(Recipe recipe, RecipeSaveAdminDTO dto) {
        recipe.setName(dto.getName());
        recipe.setImage(dto.getImage());
        recipe.setCategoryId(dto.getCategoryId());
        recipe.setCalories(dto.getCalories());
        recipe.setProtein(dto.getProtein());
        recipe.setCarbs(dto.getCarbs());
        recipe.setFat(dto.getFat());
        recipe.setDescription(dto.getDescription());
        recipe.setStatus(dto.getStatus());
        recipe.setIngredients(writeIngredients(dto.getIngredients()));
        recipe.setGoalTags(dto.getGoalTags() == null || dto.getGoalTags().isEmpty() ? null : String.join(",", dto.getGoalTags()));
    }

    private String writeIngredients(List<RecipeSaveAdminDTO.IngredientItem> ingredients) {
        if (ingredients == null) {
            return null;
        }

        List<RecipeSaveAdminDTO.IngredientItem> filtered = ingredients.stream()
                .filter(Objects::nonNull)
                .filter(item -> StringUtils.hasText(item.getName()) || StringUtils.hasText(item.getAmount()))
                .toList();

        if (filtered.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(filtered);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ResultCodeEnum.INTERNAL_SERVER_ERROR.getCode(), "食材数据保存失败");
        }
    }

    private Map<Long, RecipeCategory> buildCategoryMap(List<Recipe> recipes) {
        List<Long> categoryIds = recipes.stream()
                .map(Recipe::getCategoryId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return recipeCategoryMapper.selectBatchIds(categoryIds).stream()
                .collect(Collectors.toMap(RecipeCategory::getId, Function.identity()));
    }

    private RecipeAdminVO toRecipeAdminVO(Recipe recipe, RecipeCategory category) {
        RecipeAdminVO vo = new RecipeAdminVO();
        BeanUtils.copyProperties(recipe, vo);
        vo.setCategoryName(category == null ? "" : category.getName());
        vo.setGoalTags(parseGoalTags(recipe.getGoalTags()));
        vo.setIngredients(parseIngredients(recipe.getIngredients()));
        return vo;
    }

    private List<String> parseGoalTags(String goalTags) {
        if (!StringUtils.hasText(goalTags)) {
            return List.of();
        }
        return Arrays.stream(goalTags.split(","))
                .filter(StringUtils::hasText)
                .toList();
    }

    private List<RecipeAdminVO.IngredientItemVO> parseIngredients(String ingredients) {
        if (!StringUtils.hasText(ingredients)) {
            return List.of();
        }
        try {
            return objectMapper.readValue(
                    ingredients,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, RecipeAdminVO.IngredientItemVO.class)
            );
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
