package com.recipe.srback.service;

import com.recipe.srback.dto.UpdateEmailDTO;
import com.recipe.srback.dto.UpdateNicknameDTO;
import com.recipe.srback.dto.UpdatePasswordDTO;
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
    
    /**
     * 修改昵称
     */
    void updateNickname(Long userId, UpdateNicknameDTO dto);
    
    /**
     * 发送修改邮箱验证码
     */
    void sendUpdateEmailCode(Long userId, String newEmail);
    
    /**
     * 修改邮箱
     */
    void updateEmail(Long userId, UpdateEmailDTO dto);
    
    /**
     * 修改密码
     */
    void updatePassword(Long userId, UpdatePasswordDTO dto);
}
