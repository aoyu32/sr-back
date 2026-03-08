package com.recipe.srback.controller;

import com.recipe.srback.result.Result;
import com.recipe.srback.service.RecipeService;
import com.recipe.srback.vo.RecipeDetailVO;
import com.recipe.srback.vo.RecipeListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * 根据ID查询食谱详情
     */
    @GetMapping("/{id}")
    public Result<RecipeDetailVO> getRecipeDetailById(@PathVariable Long id) {
        log.info("查询食谱详情，ID：{}", id);
        RecipeDetailVO recipeDetail = recipeService.getRecipeDetailById(id);
        return Result.success(recipeDetail);
    }
}
