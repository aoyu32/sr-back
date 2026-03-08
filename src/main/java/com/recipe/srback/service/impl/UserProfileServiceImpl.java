package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recipe.srback.dto.UpdateProfileDTO;
import com.recipe.srback.entity.User;
import com.recipe.srback.entity.UserHealthProfile;
import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.mapper.UserMapper;
import com.recipe.srback.mapper.UserProfileMapper;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.service.UserProfileService;
import com.recipe.srback.vo.UserProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 用户信息服务实现
 */
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    
    private final UserProfileMapper userProfileMapper;
    private final UserMapper userMapper;
    
    /**
     * 获取用户信息
     */
    @Override
    public UserProfileVO getUserProfile(Long userId) {
        UserProfileVO userProfile = userProfileMapper.getUserProfileById(userId);
        
        if (userProfile == null) {
            throw new BusinessException(ResultCodeEnum.USER_NOT_FOUND);
        }
        
        return userProfile;
    }
    
    /**
     * 更新用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserProfile(Long userId, UpdateProfileDTO updateProfileDTO) {
        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.USER_NOT_FOUND);
        }
        
        // 更新用户基本信息
        if (updateProfileDTO.getNickname() != null && !updateProfileDTO.getNickname().trim().isEmpty()) {
            user.setNickname(updateProfileDTO.getNickname().trim());
        }
        if (updateProfileDTO.getAvatar() != null) {
            user.setAvatar(updateProfileDTO.getAvatar());
        }
        if (updateProfileDTO.getGender() != null) {
            user.setGender(updateProfileDTO.getGender());
        }
        if (updateProfileDTO.getBirthday() != null) {
            user.setBirthday(updateProfileDTO.getBirthday());
        }
        
        userMapper.updateById(user);
        
        // 更新健康档案
        if (updateProfileDTO.getHeight() != null || updateProfileDTO.getWeight() != null) {
            // 查询健康档案
            LambdaQueryWrapper<UserHealthProfile> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserHealthProfile::getUserId, userId);
            UserHealthProfile healthProfile = userProfileMapper.selectOne(wrapper);
            
            if (healthProfile == null) {
                // 创建新的健康档案
                healthProfile = new UserHealthProfile();
                healthProfile.setUserId(userId);
                healthProfile.setHeight(updateProfileDTO.getHeight());
                healthProfile.setWeight(updateProfileDTO.getWeight());
                
                // 计算BMI
                calculateBMI(healthProfile);
                
                userProfileMapper.insert(healthProfile);
            } else {
                // 更新健康档案
                if (updateProfileDTO.getHeight() != null) {
                    healthProfile.setHeight(updateProfileDTO.getHeight());
                }
                if (updateProfileDTO.getWeight() != null) {
                    healthProfile.setWeight(updateProfileDTO.getWeight());
                }
                
                // 重新计算BMI
                calculateBMI(healthProfile);
                
                userProfileMapper.updateById(healthProfile);
            }
        }
    }
    
    /**
     * 计算BMI和状态
     */
    private void calculateBMI(UserHealthProfile healthProfile) {
        if (healthProfile.getHeight() != null && healthProfile.getWeight() != null 
                && healthProfile.getHeight().compareTo(BigDecimal.ZERO) > 0) {
            // BMI = 体重(kg) / 身高(m)^2
            BigDecimal heightInMeters = healthProfile.getHeight().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal bmi = healthProfile.getWeight().divide(
                    heightInMeters.multiply(heightInMeters), 1, RoundingMode.HALF_UP);
            
            healthProfile.setBmi(bmi);
            
            // 判断BMI状态
            if (bmi.compareTo(new BigDecimal("18.5")) < 0) {
                healthProfile.setBmiStatus("underweight");
            } else if (bmi.compareTo(new BigDecimal("24")) < 0) {
                healthProfile.setBmiStatus("normal");
            } else if (bmi.compareTo(new BigDecimal("28")) < 0) {
                healthProfile.setBmiStatus("overweight");
            } else {
                healthProfile.setBmiStatus("obese");
            }
        }
    }
}
