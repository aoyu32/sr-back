package com.recipe.srback.controller;

import com.recipe.srback.dto.UpdateEmailDTO;
import com.recipe.srback.dto.UpdateNicknameDTO;
import com.recipe.srback.dto.UpdatePasswordDTO;
import com.recipe.srback.dto.UpdateProfileDTO;
import com.recipe.srback.result.Result;
import com.recipe.srback.service.UserProfileService;
import com.recipe.srback.vo.UserProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户信息控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserProfileController {
    
    private final UserProfileService userProfileService;
    
    /**
     * 获取用户信息
     */
    @GetMapping("/profile")
    public Result<UserProfileVO> getUserProfile(@RequestAttribute("userId") Long userId) {
        UserProfileVO userProfile = userProfileService.getUserProfile(userId);
        return Result.success(userProfile);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public Result<Void> updateUserProfile(
            @RequestAttribute("userId") Long userId,
            @RequestBody UpdateProfileDTO updateProfileDTO) {
        userProfileService.updateUserProfile(userId, updateProfileDTO);
        return Result.success();
    }
    
    /**
     * 修改昵称
     */
    @PutMapping("/profile/nickname")
    public Result<Void> updateNickname(
            @RequestAttribute("userId") Long userId,
            @RequestBody UpdateNicknameDTO dto) {
        userProfileService.updateNickname(userId, dto);
        return Result.success();
    }
    
    /**
     * 发送修改邮箱验证码
     */
    @PostMapping("/profile/email/send-code")
    public Result<Void> sendUpdateEmailCode(
            @RequestAttribute("userId") Long userId,
            @RequestParam String newEmail) {
        userProfileService.sendUpdateEmailCode(userId, newEmail);
        return Result.success();
    }
    
    /**
     * 修改邮箱
     */
    @PutMapping("/profile/email")
    public Result<Void> updateEmail(
            @RequestAttribute("userId") Long userId,
            @RequestBody UpdateEmailDTO dto) {
        userProfileService.updateEmail(userId, dto);
        return Result.success();
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/profile/password")
    public Result<Void> updatePassword(
            @RequestAttribute("userId") Long userId,
            @RequestBody UpdatePasswordDTO dto) {
        userProfileService.updatePassword(userId, dto);
        return Result.success();
    }
}
