package com.recipe.srback.service;

import com.recipe.srback.dto.UpdateProfileDTO;
import com.recipe.srback.vo.UserProfileVO;

/**
 * 用户信息服务接口
 */
public interface UserProfileService {
    
    /**
     * 获取用户信息
     */
    UserProfileVO getUserProfile(Long userId);
    
    /**
     * 更新用户信息
     */
    void updateUserProfile(Long userId, UpdateProfileDTO updateProfileDTO);
}
