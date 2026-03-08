package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recipe.srback.dto.UpdateHealthProfileDTO;
import com.recipe.srback.entity.User;
import com.recipe.srback.entity.UserHealthProfile;
import com.recipe.srback.mapper.UserHealthProfileMapper;
import com.recipe.srback.mapper.UserMapper;
import com.recipe.srback.service.HealthProfileService;
import com.recipe.srback.vo.HealthProfileVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 健康档案服务实现类
 */
@Slf4j
@Service
public class HealthProfileServiceImpl implements HealthProfileService {
    
    @Autowired
    private UserHealthProfileMapper healthProfileMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public HealthProfileVO getHealthProfile(Long userId) {
        // 使用多表查询获取健康档案和用户性别
        HealthProfileVO vo = healthProfileMapper.selectHealthProfileByUserId(userId);
        
        if (vo == null) {
            // 如果没有查询到数据，创建一个空的VO，只包含userId
            vo = new HealthProfileVO();
            vo.setUserId(userId);
            
            // 查询用户性别
            User user = userMapper.selectById(userId);
            if (user != null) {
                vo.setGender(user.getGender());
            }
        }
        
        return vo;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHealthProfile(Long userId, UpdateHealthProfileDTO dto) {
        // 查询是否已存在健康档案
        LambdaQueryWrapper<UserHealthProfile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserHealthProfile::getUserId, userId);
        UserHealthProfile profile = healthProfileMapper.selectOne(wrapper);
        
        if (profile == null) {
            // 创建新档案
            profile = new UserHealthProfile();
            profile.setUserId(userId);
        }
        
        // 更新字段
        if (dto.getHeight() != null) {
            profile.setHeight(dto.getHeight());
        }
        if (dto.getWeight() != null) {
            profile.setWeight(dto.getWeight());
        }
        if (dto.getAge() != null) {
            profile.setAge(dto.getAge());
        }
        if (dto.getActivityLevel() != null) {
            profile.setActivityLevel(dto.getActivityLevel());
        }
        if (dto.getBloodPressure() != null) {
            profile.setBloodPressure(dto.getBloodPressure());
        }
        if (dto.getBloodSugar() != null) {
            profile.setBloodSugar(dto.getBloodSugar());
        }
        
        // 计算BMI
        if (profile.getHeight() != null && profile.getWeight() != null) {
            BigDecimal heightInMeters = profile.getHeight().divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            BigDecimal bmi = profile.getWeight().divide(heightInMeters.multiply(heightInMeters), 1, RoundingMode.HALF_UP);
            profile.setBmi(bmi);
            
            // 设置BMI状态
            if (bmi.compareTo(new BigDecimal("18.5")) < 0) {
                profile.setBmiStatus("underweight");
            } else if (bmi.compareTo(new BigDecimal("24")) < 0) {
                profile.setBmiStatus("normal");
            } else if (bmi.compareTo(new BigDecimal("28")) < 0) {
                profile.setBmiStatus("overweight");
            } else {
                profile.setBmiStatus("obese");
            }
        }
        
        // 更新性别到user表
        if (dto.getGender() != null) {
            User user = new User();
            user.setId(userId);
            user.setGender(dto.getGender());
            userMapper.updateById(user);
        }
        
        // 保存或更新
        if (profile.getId() == null) {
            healthProfileMapper.insert(profile);
        } else {
            healthProfileMapper.updateById(profile);
        }
    }
}
