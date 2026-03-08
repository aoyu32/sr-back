package com.recipe.srback.service;

import com.recipe.srback.dto.AddDietPreferenceDTO;
import com.recipe.srback.dto.UpdateDietPreferenceDTO;
import com.recipe.srback.vo.DietPreferenceVO;

import java.util.List;

/**
 * 饮食偏好服务接口
 */
public interface DietPreferenceService {
    /**
     * 添加饮食偏好
     */
    void addPreference(Long userId, AddDietPreferenceDTO dto);
    
    /**
     * 获取用户的所有饮食偏好
     */
    List<DietPreferenceVO> getUserPreferences(Long userId);
    
    /**
     * 更新饮食偏好
     */
    void updatePreference(Long userId, Long preferenceId, UpdateDietPreferenceDTO dto);
    
    /**
     * 删除饮食偏好
     */
    void deletePreference(Long userId, Long preferenceId);
}
