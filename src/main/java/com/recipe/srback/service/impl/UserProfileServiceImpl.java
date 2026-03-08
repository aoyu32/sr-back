package com.recipe.srback.service.impl;

import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.mapper.UserHealthProfileMapper;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.service.UserProfileService;
import com.recipe.srback.vo.UserProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户信息服务实现
 */
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    
    private final UserHealthProfileMapper userHealthProfileMapper;
    
    /**
     * 获取用户信息
     */
    @Override
    public UserProfileVO getUserProfile(Long userId) {
        UserProfileVO userProfile = userHealthProfileMapper.getUserProfileById(userId);
        
        if (userProfile == null) {
            throw new BusinessException(ResultCodeEnum.USER_NOT_FOUND);
        }
        
        return userProfile;
    }
}
