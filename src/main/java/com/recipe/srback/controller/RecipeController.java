package com.recipe.srback.controller;

import com.recipe.srback.result.Result;
import com.recipe.srback.service.RecipeService;
import com.recipe.srback.vo.RecipeDetailVO;
import com.recipe.srback.vo.RecipeListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 食谱控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/recipe")
public class RecipeController {
    
    @Autowired
    private RecipeService recipeService;
    
    /**
     * 查询所有食谱
     */
    @GetMapping("/list")
    public Result<List<RecipeListVO>> getAllRecipes() {
        log.info("查询所有食谱");
        List<RecipeListVO> recipes = recipeService.getAllRecipes();
        return Result.success(recipes);
    }
    
    /**
     * 根据分类查询食谱
     */
    @GetMapping("/list/category/{categoryCode}")
    public Result<List<RecipeListVO>> getRecipesByCategory(@PathVariable String categoryCode) {
        log.info("根据分类查询食谱，分类code：{}", categoryCode);
        List<RecipeListVO> recipes = recipeService.getRecipesByCategory(categoryCode);
        return Result.success(recipes);
    }
    
    /**
     * 根据健康目标标签查询食谱
     */
    @GetMapping("/list/goal/{goalTag}")
    public Result<List<RecipeListVO>> getRecipesByGoalTag(@PathVariable String goalTag) {
        log.info("根据健康目标标签查询食谱，标签：{}", goalTag);
        List<RecipeListVO> recipes = recipeService.getRecipesByGoalTag(goalTag);
        return Result.success(recipes);
    }
    
    /**
     * 根据分类和健康目标标签查询食谱
     */
    @GetMapping("/list/category/{categoryCode}/goal/{goalTag}")
    public Result<List<RecipeListVO>> getRecipesByCategoryAndGoalTag(
            @PathVariable String categoryCode,
            @PathVariable String goalTag) {
        log.info("根据分类和健康目标标签查询食谱，分类code：{}，标签：{}", categoryCode, goalTag);
        List<RecipeListVO> recipes = recipeService.getRecipesByCategoryAndGoalTag(categoryCode, goalTag);
        return Result.success(recipes);
    }
    
    /**
     * 根据关键词搜索食谱
     */
    @GetMapping("/search")
    public Result<List<RecipeListVO>> searchRecipes(@RequestParam String keyword) {
        log.info("搜索食谱，关键词：{}", keyword);
        List<RecipeListVO> recipes = recipeService.searchRecipes(keyword);
        return Result.success(recipes);
    }
    
    /**
     * 根据ID查询食谱详情
     */
    @GetMapping("/{id}")
    public Result<RecipeDetailVO> getRecipeDetailById(@PathVariable Long id, HttpServletRequest request) {
        log.info("查询食谱详情，ID：{}", id);
        
        // 从请求中获取用户ID（如果已登录）
        Long userId = (Long) request.getAttribute("userId");
        
        RecipeDetailVO recipeDetail = recipeService.getRecipeDetailById(id, userId);
        return Result.success(recipeDetail);
    }
    
    /**
     * 点赞食谱
     */
    @PostMapping("/{id}/like")
    public Result<Void> likeRecipe(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("点赞食谱，食谱ID：{}，用户ID：{}", id, userId);
        
        recipeService.likeRecipe(id, userId);
        return Result.success();
    }
    
    /**
     * 取消点赞食谱
     */
    @DeleteMapping("/{id}/like")
    public Result<Void> unlikeRecipe(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("取消点赞食谱，食谱ID：{}，用户ID：{}", id, userId);
        
        recipeService.unlikeRecipe(id, userId);
        return Result.success();
    }
    
    /**
     * 收藏食谱
     */
    @PostMapping("/{id}/collect")
    public Result<Void> collectRecipe(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("收藏食谱，食谱ID：{}，用户ID：{}", id, userId);
        
        recipeService.collectRecipe(id, userId);
        return Result.success();
    }
    
    /**
     * 取消收藏食谱
     */
    @DeleteMapping("/{id}/collect")
    public Result<Void> uncollectRecipe(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("取消收藏食谱，食谱ID：{}，用户ID：{}", id, userId);
        
        recipeService.uncollectRecipe(id, userId);
        return Result.success();
    }
}
