package com.recipe.srback.controller;

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
}
