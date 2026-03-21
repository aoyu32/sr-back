package com.recipe.srback.controller;

import com.recipe.srback.dto.RecipeSaveAdminDTO;
import com.recipe.srback.result.Result;
import com.recipe.srback.service.RecipeAdminService;
import com.recipe.srback.vo.PageAdminVO;
import com.recipe.srback.vo.RecipeAdminVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 后台食谱管理控制器
 */
@RestController
@RequestMapping("/api/admin/recipes")
@RequiredArgsConstructor
public class RecipeAdminController {

    private final RecipeAdminService recipeAdminService;

    /**
     * 分页查询食谱
     */
    @GetMapping("/list")
    public Result<PageAdminVO<RecipeAdminVO>> pageRecipes(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long categoryId) {
        return Result.success(recipeAdminService.pageRecipes(pageNum, pageSize, keyword, status, categoryId));
    }

    /**
     * 查询食谱详情
     */
    @GetMapping("/{recipeId}")
    public Result<RecipeAdminVO> getRecipeById(@PathVariable Long recipeId) {
        return Result.success(recipeAdminService.getRecipeById(recipeId));
    }

    /**
     * 新增食谱
     */
    @PostMapping
    public Result<Long> createRecipe(@Valid @RequestBody RecipeSaveAdminDTO dto) {
        return Result.success(recipeAdminService.createRecipe(dto));
    }

    /**
     * 更新食谱
     */
    @PutMapping("/{recipeId}")
    public Result<Void> updateRecipe(@PathVariable Long recipeId, @Valid @RequestBody RecipeSaveAdminDTO dto) {
        recipeAdminService.updateRecipe(recipeId, dto);
        return Result.success();
    }

    /**
     * 删除食谱
     */
    @DeleteMapping("/{recipeId}")
    public Result<Void> deleteRecipe(@PathVariable Long recipeId) {
        recipeAdminService.deleteRecipe(recipeId);
        return Result.success();
    }
}
