package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe.srback.entity.Recipe;
import com.recipe.srback.entity.RecipeCategory;
import com.recipe.srback.entity.RecipeCollection;
import com.recipe.srback.entity.RecipeLike;
import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.mapper.RecipeCategoryMapper;
import com.recipe.srback.mapper.RecipeCollectionMapper;
import com.recipe.srback.mapper.RecipeLikeMapper;
import com.recipe.srback.mapper.RecipeMapper;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.service.RecipeService;
import com.recipe.srback.vo.RecipeDetailVO;
import com.recipe.srback.vo.RecipeListVO;
import com.recipe.srback.vo.RecipeRankingVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    @Autowired
    private RecipeLikeMapper recipeLikeMapper;
    
    @Autowired
    private RecipeCollectionMapper recipeCollectionMapper;
    
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
    public RecipeDetailVO getRecipeDetailById(Long id, Long userId) {
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
        
        // 查询用户是否点赞和收藏
        boolean isLiked = false;
        boolean isCollected = false;
        
        if (userId != null) {
            LambdaQueryWrapper<RecipeLike> likeWrapper = new LambdaQueryWrapper<>();
            likeWrapper.eq(RecipeLike::getRecipeId, id);
            likeWrapper.eq(RecipeLike::getUserId, userId);
            isLiked = recipeLikeMapper.selectCount(likeWrapper) > 0;
            
            LambdaQueryWrapper<RecipeCollection> collectionWrapper = new LambdaQueryWrapper<>();
            collectionWrapper.eq(RecipeCollection::getRecipeId, id);
            collectionWrapper.eq(RecipeCollection::getUserId, userId);
            isCollected = recipeCollectionMapper.selectCount(collectionWrapper) > 0;
        }
        
        return convertToDetailVO(recipe, categoryName, isLiked, isCollected);
    }
    
    @Override
    public List<RecipeListVO> searchRecipes(String keyword) {
        LambdaQueryWrapper<Recipe> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Recipe::getStatus, 1);
        wrapper.and(w -> w.like(Recipe::getName, keyword)
                .or()
                .like(Recipe::getDescription, keyword));
        wrapper.orderByDesc(Recipe::getCreatedAt);
        
        List<Recipe> recipes = recipeMapper.selectList(wrapper);
        
        Map<Long, String> categoryMap = getCategoryCodeMap();
        
        return recipes.stream()
                .map(recipe -> convertToListVO(recipe, categoryMap))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeRecipe(Long recipeId, Long userId) {
        // 检查食谱是否存在
        Recipe recipe = recipeMapper.selectById(recipeId);
        if (recipe == null || recipe.getStatus() == 0) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        
        // 检查是否已点赞
        LambdaQueryWrapper<RecipeLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecipeLike::getRecipeId, recipeId);
        wrapper.eq(RecipeLike::getUserId, userId);
        
        if (recipeLikeMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCodeEnum.BAD_REQUEST.getCode(), "已经点赞过了");
        }
        
        // 添加点赞记录
        RecipeLike recipeLike = new RecipeLike();
        recipeLike.setRecipeId(recipeId);
        recipeLike.setUserId(userId);
        recipeLikeMapper.insert(recipeLike);
        
        // 增加点赞数
        recipe.setLikesCount(recipe.getLikesCount() + 1);
        recipeMapper.updateById(recipe);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlikeRecipe(Long recipeId, Long userId) {
        // 检查食谱是否存在
        Recipe recipe = recipeMapper.selectById(recipeId);
        if (recipe == null || recipe.getStatus() == 0) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        
        // 删除点赞记录
        LambdaQueryWrapper<RecipeLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecipeLike::getRecipeId, recipeId);
        wrapper.eq(RecipeLike::getUserId, userId);
        
        int deleted = recipeLikeMapper.delete(wrapper);
        
        if (deleted > 0) {
            // 减少点赞数
            recipe.setLikesCount(Math.max(0, recipe.getLikesCount() - 1));
            recipeMapper.updateById(recipe);
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void collectRecipe(Long recipeId, Long userId) {
        // 检查食谱是否存在
        Recipe recipe = recipeMapper.selectById(recipeId);
        if (recipe == null || recipe.getStatus() == 0) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        
        // 检查是否已收藏
        LambdaQueryWrapper<RecipeCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecipeCollection::getRecipeId, recipeId);
        wrapper.eq(RecipeCollection::getUserId, userId);
        
        if (recipeCollectionMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCodeEnum.BAD_REQUEST.getCode(), "已经收藏过了");
        }
        
        // 添加收藏记录
        RecipeCollection recipeCollection = new RecipeCollection();
        recipeCollection.setRecipeId(recipeId);
        recipeCollection.setUserId(userId);
        recipeCollectionMapper.insert(recipeCollection);
        
        // 增加收藏数
        recipe.setCollectionsCount(recipe.getCollectionsCount() + 1);
        recipeMapper.updateById(recipe);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uncollectRecipe(Long recipeId, Long userId) {
        // 检查食谱是否存在
        Recipe recipe = recipeMapper.selectById(recipeId);
        if (recipe == null || recipe.getStatus() == 0) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        
        // 删除收藏记录
        LambdaQueryWrapper<RecipeCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecipeCollection::getRecipeId, recipeId);
        wrapper.eq(RecipeCollection::getUserId, userId);
        
        int deleted = recipeCollectionMapper.delete(wrapper);
        
        if (deleted > 0) {
            // 减少收藏数
            recipe.setCollectionsCount(Math.max(0, recipe.getCollectionsCount() - 1));
            recipeMapper.updateById(recipe);
        }
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
    private RecipeDetailVO convertToDetailVO(Recipe recipe, String categoryName, boolean isLiked, boolean isCollected) {
        RecipeDetailVO vo = new RecipeDetailVO();
        vo.setId(recipe.getId().toString());
        vo.setName(recipe.getName());
        vo.setCategory(categoryName);
        vo.setImage(recipe.getImage());
        vo.setLikes(recipe.getLikesCount());
        vo.setCollections(recipe.getCollectionsCount());
        vo.setViews(recipe.getViewsCount());
        vo.setDescription(recipe.getDescription());
        vo.setIsLiked(isLiked);
        vo.setIsCollected(isCollected);
        
        // 解析goalTags为数组
        if (recipe.getGoalTags() != null && !recipe.getGoalTags().isEmpty()) {
            List<String> tags = Arrays.asList(recipe.getGoalTags().split(","));
            vo.setTags(tags);
        } else {
            vo.setTags(new ArrayList<>());
        }
        
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
    
    @Override
    public List<RecipeRankingVO> getRecipeRankings() {
        LambdaQueryWrapper<Recipe> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Recipe::getStatus, 1);
        wrapper.orderByDesc(Recipe::getLikesCount);
        wrapper.last("LIMIT 10");
        
        List<Recipe> recipes = recipeMapper.selectList(wrapper);
        
        List<RecipeRankingVO> rankings = new ArrayList<>();
        for (int i = 0; i < recipes.size(); i++) {
            Recipe recipe = recipes.get(i);
            RecipeRankingVO vo = new RecipeRankingVO();
            vo.setRank(i + 1);
            vo.setId(recipe.getId());
            vo.setImage(recipe.getImage());
            vo.setName(recipe.getName());
            vo.setLikes(recipe.getLikesCount());
            vo.setViews(recipe.getViewsCount());
            rankings.add(vo);
        }
        
        return rankings;
    }
}
