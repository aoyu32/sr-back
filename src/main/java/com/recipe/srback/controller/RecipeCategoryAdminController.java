package com.recipe.srback.controller;

import com.recipe.srback.dto.RecipeCategorySaveAdminDTO;
import com.recipe.srback.result.Result;
import com.recipe.srback.service.RecipeCategoryAdminService;
import com.recipe.srback.vo.RecipeCategoryAdminVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台食谱分类管理控制器
 */
@RestController
@RequestMapping("/api/admin/recipe-categories")
@RequiredArgsConstructor
public class RecipeCategoryAdminController {

    private final RecipeCategoryAdminService recipeCategoryAdminService;

    /**
     * 查询分类列表
     */
    @GetMapping("/list")
    public Result<List<RecipeCategoryAdminVO>> listCategories(@RequestParam(required = false) String keyword) {
        return Result.success(recipeCategoryAdminService.listCategories(keyword));
    }

    /**
     * 查询分类详情
     */
    @GetMapping("/{categoryId}")
    public Result<RecipeCategoryAdminVO> getCategoryById(@PathVariable Long categoryId) {
        return Result.success(recipeCategoryAdminService.getCategoryById(categoryId));
    }

    /**
     * 新增分类
     */
    @PostMapping
    public Result<Long> createCategory(@Valid @RequestBody RecipeCategorySaveAdminDTO dto) {
        return Result.success(recipeCategoryAdminService.createCategory(dto));
    }

    /**
     * 更新分类
     */
    @PutMapping("/{categoryId}")
    public Result<Void> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody RecipeCategorySaveAdminDTO dto) {
        recipeCategoryAdminService.updateCategory(categoryId, dto);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{categoryId}")
    public Result<Void> deleteCategory(@PathVariable Long categoryId) {
        recipeCategoryAdminService.deleteCategory(categoryId);
        return Result.success();
    }
}
