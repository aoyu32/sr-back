package com.recipe.srback.controller;

import com.recipe.srback.dto.CreateRecipeDTO;
import com.recipe.srback.dto.UpdateRecipeDTO;
import com.recipe.srback.result.Result;
import com.recipe.srback.service.MyRecipeService;
import com.recipe.srback.vo.RecipeEditVO;
import com.recipe.srback.vo.RecipeListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 我的食谱控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/my-recipe")
public class MyRecipeController {
    
    @Autowired
    private MyRecipeService myRecipeService;
    
    /**
     * 创建我的食谱
     */
    @PostMapping("/create")
    public Result<Long> createRecipe(@RequestAttribute("userId") Long userId,
                                     @RequestBody CreateRecipeDTO dto) {
        log.info("创建我的食谱，userId: {}, dto: {}", userId, dto);
        Long recipeId = myRecipeService.createRecipe(userId, dto);
        return Result.success(recipeId);
    }
    
    /**
     * 获取我的食谱列表
     */
    @GetMapping("/list")
    public Result<List<RecipeListVO>> getMyRecipes(@RequestAttribute("userId") Long userId) {
        log.info("获取我的食谱列表，userId: {}", userId);
        List<RecipeListVO> recipes = myRecipeService.getMyRecipes(userId);
        return Result.success(recipes);
    }
    
    /**
     * 获取我的食谱详情（用于编辑）
     */
    @GetMapping("/{recipeId}")
    public Result<RecipeEditVO> getMyRecipeById(@RequestAttribute("userId") Long userId,
                                                 @PathVariable Long recipeId) {
        log.info("获取我的食谱详情，userId: {}, recipeId: {}", userId, recipeId);
        RecipeEditVO recipe = myRecipeService.getMyRecipeById(userId, recipeId);
        return Result.success(recipe);
    }
    
    /**
     * 更新我的食谱
     */
    @PutMapping("/update/{recipeId}")
    public Result<Void> updateRecipe(@RequestAttribute("userId") Long userId,
                                     @PathVariable Long recipeId,
                                     @RequestBody UpdateRecipeDTO dto) {
        log.info("更新我的食谱，userId: {}, recipeId: {}, dto: {}", userId, recipeId, dto);
        myRecipeService.updateRecipe(userId, recipeId, dto);
        return Result.success();
    }
    
    /**
     * 删除我的食谱
     */
    @DeleteMapping("/delete/{recipeId}")
    public Result<Void> deleteRecipe(@RequestAttribute("userId") Long userId,
                                     @PathVariable Long recipeId) {
        log.info("删除我的食谱，userId: {}, recipeId: {}", userId, recipeId);
        myRecipeService.deleteRecipe(userId, recipeId);
        return Result.success();
    }
}
