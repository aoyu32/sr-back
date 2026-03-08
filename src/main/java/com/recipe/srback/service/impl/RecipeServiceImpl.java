package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe.srback.entity.Recipe;
import com.recipe.srback.entity.RecipeCategory;
import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.mapper.RecipeCategoryMapper;
import com.recipe.srback.mapper.RecipeMapper;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.service.RecipeService;
import com.recipe.srback.vo.RecipeDetailVO;
import com.recipe.srback.vo.RecipeListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 食谱服务实现类
 */
@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {
    
    @Autowired
    private RecipeMapper recipeMapper;
    
    @Autowired
    private RecipeCategoryMapper recipeCategoryMapper;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public List<RecipeListVO> getAllRecipes() {
        LambdaQueryWrapper<Recipe> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Recipe::getStatus, 1);
        wrapper.orderByDesc(Recipe::getCreatedAt);
        
        List<Recipe> recipes = recipeMapper.selectList(wrapper);
        
        // 获取所有分类
        Map<Long, String> categoryMap = getCategoryCodeMap();
        
        return recipes.stream()
                .map(recipe -> convertToListVO(recipe, categoryMap))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<RecipeListVO> getRecipesByCategory(String categoryCode) {
        // 根据分类code查询分类ID
        LambdaQueryWrapper<RecipeCategory> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.eq(RecipeCategory::getCode, categoryCode);
        RecipeCategory category = recipeCategoryMapper.selectOne(categoryWrapper);
        
        if (category == null) {
            throw new BusinessException(ResultCodeEnum.PARAM_ERROR);
        }
        
        LambdaQueryWrapper<Recipe> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Recipe::getCategoryId, category.getId());
        wrapper.eq(Recipe::getStatus, 1);
        wrapper.orderByDesc(Recipe::getCreatedAt);
        
        List<Recipe> recipes = recipeMapper.selectList(wrapper);
        
        Map<Long, String> categoryMap = getCategoryCodeMap();
        
        return recipes.stream()
                .map(recipe -> convertToListVO(recipe, categoryMap))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<RecipeListVO> getRecipesByGoalTag(String goalTag) {
        LambdaQueryWrapper<Recipe> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Recipe::getGoalTags, goalTag);
        wrapper.eq(Recipe::getStatus, 1);
        wrapper.orderByDesc(Recipe::getCreatedAt);
        
        List<Recipe> recipes = recipeMapper.selectList(wrapper);
        
        Map<Long, String> categoryMap = getCategoryCodeMap();
        
        return recipes.stream()
                .map(recipe -> convertToListVO(recipe, categoryMap))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<RecipeListVO> getRecipesByCategoryAndGoalTag(String categoryCode, String goalTag) {
        // 根据分类code查询分类ID
        LambdaQueryWrapper<RecipeCategory> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.eq(RecipeCategory::getCode, categoryCode);
        RecipeCategory category = recipeCategoryMapper.selectOne(categoryWrapper);
        
        if (category == null) {
            throw new BusinessException(ResultCodeEnum.PARAM_ERROR);
        }
        
        LambdaQueryWrapper<Recipe> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Recipe::getCategoryId, category.getId());
        wrapper.like(Recipe::getGoalTags, goalTag);
        wrapper.eq(Recipe::getStatus, 1);
        wrapper.orderByDesc(Recipe::getCreatedAt);
        
        List<Recipe> recipes = recipeMapper.selectList(wrapper);
        
        Map<Long, String> categoryMap = getCategoryCodeMap();
        
        return recipes.stream()
                .map(recipe -> convertToListVO(recipe, categoryMap))
                .collect(Collectors.toList());
    }
    
    @Override
    public RecipeDetailVO getRecipeDetailById(Long id) {
        Recipe recipe = recipeMapper.selectById(id);
        
        if (recipe == null || recipe.getStatus() == 0) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        
        // 增加浏览量
        recipe.setViewsCount(recipe.getViewsCount() + 1);
        recipeMapper.updateById(recipe);
        
        // 查询分类名称
        RecipeCategory category = recipeCategoryMapper.selectById(recipe.getCategoryId());
        String categoryName = category != null ? category.getName() : "";
        
        return convertToDetailVO(recipe, categoryName);
    }
    
    /**
     * 获取分类ID到Code的映射
     */
    private Map<Long, String> getCategoryCodeMap() {
        List<RecipeCategory> categories = recipeCategoryMapper.selectList(null);
        return categories.stream()
                .collect(Collectors.toMap(RecipeCategory::getId, RecipeCategory::getCode));
    }
    
    /**
     * 转换为列表VO
     */
    private RecipeListVO convertToListVO(Recipe recipe, Map<Long, String> categoryMap) {
        RecipeListVO vo = new RecipeListVO();
        vo.setId(recipe.getId());
        vo.setName(recipe.getName());
        vo.setKcal(recipe.getCalories());
        vo.setCategoryId(categoryMap.get(recipe.getCategoryId()));
        vo.setCover(recipe.getImage());
        
        // 解析goalTags为数组
        if (recipe.getGoalTags() != null && !recipe.getGoalTags().isEmpty()) {
            List<String> tags = Arrays.asList(recipe.getGoalTags().split(","));
            vo.setTags(tags);
        } else {
            vo.setTags(new ArrayList<>());
        }
        
        return vo;
    }
    
    /**
     * 转换为详情VO
     */
    private RecipeDetailVO convertToDetailVO(Recipe recipe, String categoryName) {
        RecipeDetailVO vo = new RecipeDetailVO();
        vo.setId(recipe.getId().toString());
        vo.setName(recipe.getName());
        vo.setCategory(categoryName);
        vo.setImage(recipe.getImage());
        vo.setLikes(recipe.getLikesCount());
        vo.setCollections(recipe.getCollectionsCount());
        vo.setViews(recipe.getViewsCount());
        vo.setDescription(recipe.getDescription());
        
        // 营养信息
        RecipeDetailVO.NutritionVO nutrition = new RecipeDetailVO.NutritionVO();
        nutrition.setCalories(recipe.getCalories());
        nutrition.setProtein(recipe.getProtein() != null ? recipe.getProtein().toString() : "0");
        nutrition.setCarbs(recipe.getCarbs() != null ? recipe.getCarbs().toString() : "0");
        nutrition.setFat(recipe.getFat() != null ? recipe.getFat().toString() : "0");
        vo.setNutrition(nutrition);
        
        // 解析食材JSON
        try {
            if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
                List<RecipeDetailVO.IngredientVO> ingredients = objectMapper.readValue(
                        recipe.getIngredients(),
                        new TypeReference<List<RecipeDetailVO.IngredientVO>>() {}
                );
                vo.setIngredients(ingredients);
            } else {
                vo.setIngredients(new ArrayList<>());
            }
        } catch (Exception e) {
            log.error("解析食材JSON失败", e);
            vo.setIngredients(new ArrayList<>());
        }
        
        return vo;
    }
}
