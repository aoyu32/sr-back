package com.recipe.srback.controller;

import com.recipe.srback.dto.LoginDTO;
import com.recipe.srback.dto.RegisterDTO;
import com.recipe.srback.dto.ResetPasswordDTO;
import com.recipe.srback.result.Result;
import com.recipe.srback.service.UserAuthService;
import com.recipe.srback.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "用户认证", description = "用户登录、注册、密码重置等接口")
public class UserAuthController {
    
    private final UserAuthService userAuthService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "使用邮箱和密码登录")
    public Result<UserVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        UserVO userVO = userAuthService.login(loginDTO);
        return Result.success(userVO);
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "使用邮箱、验证码和密码注册")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        userAuthService.register(registerDTO);
        return Result.success();
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    @Operation(summary = "重置密码", description = "使用邮箱、验证码重置密码")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        userAuthService.resetPassword(resetPasswordDTO);
        return Result.success();
    }
    
    /**
     * 发送验证码
     */
    @PostMapping("/send-code")
    @Operation(summary = "发送验证码", description = "发送邮箱验证码")
    public Result<Void> sendVerificationCode(
            @Parameter(description = "邮箱", required = true) @RequestParam String email,
            @Parameter(description = "类型：register-注册，reset_password-重置密码", required = true) @RequestParam String type) {
        userAuthService.sendVerificationCode(email, type);
        return Result.success();
    }
}
