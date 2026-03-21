package com.recipe.srback.controller;

import com.recipe.srback.dto.LoginAdminDTO;
import com.recipe.srback.result.Result;
import com.recipe.srback.service.AuthAdminService;
import com.recipe.srback.vo.AdminLoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 后台管理员认证控制器
 */
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AuthAdminController {

    private final AuthAdminService authAdminService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<AdminLoginVO> login(@Valid @RequestBody LoginAdminDTO loginAdminDTO) {
        return Result.success(authAdminService.login(loginAdminDTO));
    }

    /**
     * 获取当前管理员资料
     */
    @GetMapping("/profile")
    public Result<AdminLoginVO> profile(@RequestAttribute("userId") Long userId) {
        return Result.success(authAdminService.getProfile(userId));
    }
}
