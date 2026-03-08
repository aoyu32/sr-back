package com.recipe.srback.controller;

import com.recipe.srback.result.Result;
import com.recipe.srback.service.RecipeCollectionService;
import com.recipe.srback.vo.RecipeListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 食谱收藏控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/recipe-collection")
public class RecipeCollectionController {
    
    @Autowired
    private RecipeCollectionService collectionService;
    
    /**
     * 获取我的收藏列表
     */
    @GetMapping("/list")
    public Result<List<RecipeListVO>> getMyCollections(@RequestAttribute("userId") Long userId) {
        log.info("获取我的收藏列表，userId: {}", userId);
        List<RecipeListVO> collections = collectionService.getMyCollections(userId);
        return Result.success(collections);
    }
    
    /**
     * 取消收藏
     */
    @DeleteMapping("/cancel/{recipeId}")
    public Result<Void> cancelCollection(@RequestAttribute("userId") Long userId,
                                         @PathVariable Long recipeId) {
        log.info("取消收藏，userId: {}, recipeId: {}", userId, recipeId);
        collectionService.cancelCollection(userId, recipeId);
        return Result.success();
    }
}
