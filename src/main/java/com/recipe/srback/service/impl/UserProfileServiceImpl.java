package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recipe.srback.dto.UpdateEmailDTO;
import com.recipe.srback.dto.UpdateNicknameDTO;
import com.recipe.srback.dto.UpdatePasswordDTO;
import com.recipe.srback.dto.UpdateProfileDTO;
import com.recipe.srback.entity.User;
import com.recipe.srback.entity.UserHealthProfile;
import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.mapper.UserMapper;
import com.recipe.srback.mapper.UserProfileMapper;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.service.EmailService;
import com.recipe.srback.service.UserProfileService;
import com.recipe.srback.utils.PasswordUtil;
import com.recipe.srback.utils.VerificationCodeUtil;
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
    private final EmailService emailService;
    private final VerificationCodeUtil verificationCodeUtil;
    private final PasswordUtil passwordUtil;
    
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
    
    /**
     * 修改昵称
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNickname(Long userId, UpdateNicknameDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.USER_NOT_FOUND);
        }
        
        if (dto.getNickname() == null || dto.getNickname().trim().isEmpty()) {
            throw new BusinessException(ResultCodeEnum.PARAM_ERROR);
        }
        
        user.setNickname(dto.getNickname().trim());
        userMapper.updateById(user);
    }
    
    /**
     * 发送修改邮箱验证码
     */
    @Override
    public void sendUpdateEmailCode(Long userId, String newEmail) {
        // 检查新邮箱是否已被使用
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, newEmail);
        User existUser = userMapper.selectOne(wrapper);
        
        if (existUser != null) {
            throw new BusinessException(ResultCodeEnum.EMAIL_ALREADY_EXISTS);
        }
        
        // 生成6位数字验证码
        String code = String.format("%06d", new java.util.Random().nextInt(999999));
        
        // 保存验证码到内存
        verificationCodeUtil.saveCode(newEmail, code, "update_email");
        
        // 发送邮件
        emailService.sendVerificationCode(newEmail, code, "update_email");
    }
    
    /**
     * 修改邮箱
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmail(Long userId, UpdateEmailDTO dto) {
        // 验证验证码
        if (!verificationCodeUtil.verifyCode(dto.getNewEmail(), dto.getVerificationCode(), "update_email")) {
            throw new BusinessException(ResultCodeEnum.VERIFICATION_CODE_ERROR);
        }
        
        // 检查新邮箱是否已被使用
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, dto.getNewEmail());
        User existUser = userMapper.selectOne(wrapper);
        
        if (existUser != null) {
            throw new BusinessException(ResultCodeEnum.EMAIL_ALREADY_EXISTS);
        }
        
        // 更新邮箱
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.USER_NOT_FOUND);
        }
        
        user.setEmail(dto.getNewEmail());
        userMapper.updateById(user);
    }
    
    /**
     * 修改密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long userId, UpdatePasswordDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.USER_NOT_FOUND);
        }
        
        // 验证旧密码
        if (!passwordUtil.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCodeEnum.OLD_PASSWORD_ERROR);
        }
        
        // 加密新密码
        String encryptedNewPassword = passwordUtil.encode(dto.getNewPassword());
        user.setPassword(encryptedNewPassword);
        userMapper.updateById(user);
    }
}
