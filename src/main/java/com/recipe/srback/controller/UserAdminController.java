package com.recipe.srback.controller;

import com.recipe.srback.dto.UserCreateAdminDTO;
import com.recipe.srback.dto.UserUpdateAdminDTO;
import com.recipe.srback.result.Result;
import com.recipe.srback.service.UserAdminService;
import com.recipe.srback.vo.PageAdminVO;
import com.recipe.srback.vo.UserAdminVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 后台用户管理控制器
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;

    /**
     * 分页查询用户
     */
    @GetMapping("/list")
    public Result<PageAdminVO<UserAdminVO>> pageUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(userAdminService.pageUsers(pageNum, pageSize, keyword, status));
    }

    /**
     * 查询用户详情
     */
    @GetMapping("/{userId}")
    public Result<UserAdminVO> getUserById(@PathVariable Long userId) {
        return Result.success(userAdminService.getUserById(userId));
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<Long> createUser(@Valid @RequestBody UserCreateAdminDTO dto) {
        return Result.success(userAdminService.createUser(dto));
    }

    /**
     * 编辑用户
     */
    @PutMapping("/{userId}")
    public Result<Void> updateUser(@PathVariable Long userId, @Valid @RequestBody UserUpdateAdminDTO dto) {
        userAdminService.updateUser(userId, dto);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    public Result<Void> deleteUser(@PathVariable Long userId) {
        userAdminService.deleteUser(userId);
        return Result.success();
    }
}
