package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe.srback.dto.CreateRecipeDTO;
import com.recipe.srback.dto.UpdateRecipeDTO;
import com.recipe.srback.entity.Recipe;
import com.recipe.srback.entity.RecipeCategory;
import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.mapper.RecipeCategoryMapper;
import com.recipe.srback.mapper.RecipeMapper;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.service.MyRecipeService;
import com.recipe.srback.vo.RecipeEditVO;
import com.recipe.srback.vo.RecipeListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 我的食谱服务实现类
 */
@Slf4j
@Service
public class MyRecipeServiceImpl implements MyRecipeService {
    
    @Autowired
    private RecipeMapper recipeMapper;
    
    @Autowired
    private RecipeCategoryMapper categoryMapper;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRecipe(Long userId, CreateRecipeDTO dto) {
        log.info("创建食谱 - userId: {}, dto: {}", userId, dto);
        log.info("categoryId(code): {}", dto.getCategoryId());
        
        // 根据分类code查询分类ID
        LambdaQueryWrapper<RecipeCategory> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.eq(RecipeCategory::getCode, dto.getCategoryId());
        RecipeCategory category = categoryMapper.selectOne(categoryWrapper);
        
        if (category == null) {
            throw new BusinessException(ResultCodeEnum.PARAM_ERROR);
        }
        
        Recipe recipe = new Recipe();
        recipe.setName(dto.getName());
        recipe.setImage(dto.getImage());
        recipe.setCategoryId(category.getId()); // 使用数据库ID
        recipe.setCalories(dto.getCalories());
        recipe.setProtein(dto.getProtein());
        recipe.setCarbs(dto.getCarbs());
        recipe.setFat(dto.getFat());
        recipe.setDescription(dto.getDescription());
        recipe.setCreatorId(userId);
        recipe.setStatus(1);
        recipe.setLikesCount(0);
        recipe.setCollectionsCount(0);
        recipe.setViewsCount(0);
        
        log.info("Recipe对象 - categoryId: {}", recipe.getCategoryId());
        
        // 处理食材列表（转为JSON）
        if (dto.getIngredients() != null && !dto.getIngredients().isEmpty()) {
            try {
                String ingredientsJson = objectMapper.writeValueAsString(dto.getIngredients());
                recipe.setIngredients(ingredientsJson);
            } catch (JsonProcessingException e) {
                log.error("食材列表转JSON失败", e);
                throw new BusinessException(ResultCodeEnum.INTERNAL_SERVER_ERROR);
            }
        }
        
        // 处理健康目标标签（逗号分隔）
        if (dto.getGoalTags() != null && !dto.getGoalTags().isEmpty()) {
            String goalTags = String.join(",", dto.getGoalTags());
            recipe.setGoalTags(goalTags);
        }
        
        recipeMapper.insert(recipe);
        
        return recipe.getId();
    }
    
    @Override
    public List<RecipeListVO> getMyRecipes(Long userId) {
        LambdaQueryWrapper<Recipe> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Recipe::getCreatorId, userId);
        wrapper.orderByDesc(Recipe::getCreatedAt);
        
        List<Recipe> recipes = recipeMapper.selectList(wrapper);
        
        return recipes.stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public RecipeEditVO getMyRecipeById(Long userId, Long recipeId) {
        Recipe recipe = recipeMapper.selectById(recipeId);
        
        if (recipe == null || !recipe.getCreatorId().equals(userId)) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        
        // 查询分类code
        RecipeCategory category = categoryMapper.selectById(recipe.getCategoryId());
        String categoryCode = category != null ? category.getCode() : null;
        
        RecipeEditVO vo = new RecipeEditVO();
        vo.setId(recipe.getId());
        vo.setName(recipe.getName());
        vo.setImage(recipe.getImage());
        vo.setCategoryId(categoryCode);
        vo.setCalories(recipe.getCalories());
        vo.setProtein(recipe.getProtein());
        vo.setCarbs(recipe.getCarbs());
        vo.setFat(recipe.getFat());
        vo.setDescription(recipe.getDescription());
        
        // 处理食材列表
        if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
            try {
                List<RecipeEditVO.IngredientVO> ingredients = objectMapper.readValue(
                    recipe.getIngredients(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, RecipeEditVO.IngredientVO.class)
                );
                vo.setIngredients(ingredients);
            } catch (JsonProcessingException e) {
                log.error("解析食材列表失败", e);
                vo.setIngredients(List.of());
            }
        } else {
            vo.setIngredients(List.of());
        }
        
        // 处理健康目标标签
        if (recipe.getGoalTags() != null && !recipe.getGoalTags().isEmpty()) {
            List<String> tags = Arrays.asList(recipe.getGoalTags().split(","));
            vo.setGoalTags(tags);
        } else {
            vo.setGoalTags(List.of());
        }
        
        return vo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRecipe(Long userId, Long recipeId, UpdateRecipeDTO dto) {
        Recipe recipe = recipeMapper.selectById(recipeId);
        
        if (recipe == null || !recipe.getCreatorId().equals(userId)) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        
        // 根据分类code查询分类ID
        LambdaQueryWrapper<RecipeCategory> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.eq(RecipeCategory::getCode, dto.getCategoryId());
        RecipeCategory category = categoryMapper.selectOne(categoryWrapper);
        
        if (category == null) {
            throw new BusinessException(ResultCodeEnum.PARAM_ERROR);
        }
        
        recipe.setName(dto.getName());
        recipe.setImage(dto.getImage());
        recipe.setCategoryId(category.getId()); // 使用数据库ID
        recipe.setCalories(dto.getCalories());
        recipe.setProtein(dto.getProtein());
        recipe.setCarbs(dto.getCarbs());
        recipe.setFat(dto.getFat());
        recipe.setDescription(dto.getDescription());
        
        // 处理食材列表
        if (dto.getIngredients() != null) {
            try {
                String ingredientsJson = objectMapper.writeValueAsString(dto.getIngredients());
                recipe.setIngredients(ingredientsJson);
            } catch (JsonProcessingException e) {
                log.error("食材列表转JSON失败", e);
                throw new BusinessException(ResultCodeEnum.INTERNAL_SERVER_ERROR);
            }
        }
        
        // 处理健康目标标签
        if (dto.getGoalTags() != null) {
            String goalTags = dto.getGoalTags().isEmpty() ? null : String.join(",", dto.getGoalTags());
            recipe.setGoalTags(goalTags);
        }
        
        recipeMapper.updateById(recipe);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRecipe(Long userId, Long recipeId) {
        Recipe recipe = recipeMapper.selectById(recipeId);
        
        if (recipe == null || !recipe.getCreatorId().equals(userId)) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        
        recipeMapper.deleteById(recipeId);
    }
    
    /**
     * 转换为列表VO
     */
    private RecipeListVO convertToListVO(Recipe recipe) {
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
