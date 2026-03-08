package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recipe.srback.dto.AddDietPreferenceDTO;
import com.recipe.srback.dto.UpdateDietPreferenceDTO;
import com.recipe.srback.entity.UserDietPreference;
import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.mapper.UserDietPreferenceMapper;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.service.DietPreferenceService;
import com.recipe.srback.vo.DietPreferenceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 饮食偏好服务实现类
 */
@Slf4j
@Service
public class DietPreferenceServiceImpl implements DietPreferenceService {
    
    @Autowired
    private UserDietPreferenceMapper preferenceMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPreference(Long userId, AddDietPreferenceDTO dto) {
        // 检查是否已存在相同的偏好
        LambdaQueryWrapper<UserDietPreference> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDietPreference::getUserId, userId);
        wrapper.eq(UserDietPreference::getPreferenceType, dto.getPreferenceType());
        wrapper.eq(UserDietPreference::getFoodName, dto.getFoodName());
        
        Long count = preferenceMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCodeEnum.BAD_REQUEST.getCode(), "该食物已在列表中");
        }
        
        UserDietPreference preference = new UserDietPreference();
        preference.setUserId(userId);
        preference.setPreferenceType(dto.getPreferenceType());
        preference.setFoodName(dto.getFoodName());
        
        preferenceMapper.insert(preference);
    }
    
    @Override
    public List<DietPreferenceVO> getUserPreferences(Long userId) {
        LambdaQueryWrapper<UserDietPreference> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDietPreference::getUserId, userId);
        wrapper.orderByDesc(UserDietPreference::getCreatedAt);
        
        List<UserDietPreference> preferences = preferenceMapper.selectList(wrapper);
        
        return preferences.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePreference(Long userId, Long preferenceId, UpdateDietPreferenceDTO dto) {
        UserDietPreference preference = preferenceMapper.selectById(preferenceId);
        
        if (preference == null || !preference.getUserId().equals(userId)) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        
        // 检查新名称是否与其他记录重复
        LambdaQueryWrapper<UserDietPreference> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDietPreference::getUserId, userId);
        wrapper.eq(UserDietPreference::getPreferenceType, preference.getPreferenceType());
        wrapper.eq(UserDietPreference::getFoodName, dto.getFoodName());
        wrapper.ne(UserDietPreference::getId, preferenceId);
        
        Long count = preferenceMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCodeEnum.BAD_REQUEST.getCode(), "该食物已在列表中");
        }
        
        preference.setFoodName(dto.getFoodName());
        preferenceMapper.updateById(preference);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePreference(Long userId, Long preferenceId) {
        UserDietPreference preference = preferenceMapper.selectById(preferenceId);
        
        if (preference == null || !preference.getUserId().equals(userId)) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        
        preferenceMapper.deleteById(preferenceId);
    }
    
    /**
     * 转换为VO
     */
    private DietPreferenceVO convertToVO(UserDietPreference preference) {
        DietPreferenceVO vo = new DietPreferenceVO();
        vo.setId(preference.getId());
        vo.setPreferenceType(preference.getPreferenceType());
        vo.setFoodName(preference.getFoodName());
        
        return vo;
    }
}
