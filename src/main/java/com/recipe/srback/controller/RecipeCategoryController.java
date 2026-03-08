package com.recipe.srback.controller;

import com.recipe.srback.result.Result;
import com.recipe.srback.service.RecipeCategoryService;
import com.recipe.srback.vo.RecipeCategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 食谱分类控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/recipe/category")
public class RecipeCategoryController {
    
    @Autowired
    private RecipeCategoryService recipeCategoryService;
    
    /**
     * 查询所有分类
     */
    @GetMapping("/list")
    public Result<List<RecipeCategoryVO>> getAllCategories() {
        log.info("查询所有食谱分类");
        List<RecipeCategoryVO> categories = recipeCategoryService.getAllCategories();
        return Result.success(categories);
    }
}
