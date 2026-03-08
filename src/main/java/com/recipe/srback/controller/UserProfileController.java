package com.recipe.srback.controller;

import com.recipe.srback.result.Result;
import com.recipe.srback.service.UserProfileService;
import com.recipe.srback.vo.UserProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
