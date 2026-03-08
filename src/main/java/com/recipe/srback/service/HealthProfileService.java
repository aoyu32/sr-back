package com.recipe.srback.service;

import com.recipe.srback.dto.UpdateHealthProfileDTO;
import com.recipe.srback.vo.HealthProfileVO;

/**
 * 健康档案服务接口
 */
public interface HealthProfileService {
    
    /**
     * 查询用户健康档案
     */
    HealthProfileVO getHealthProfile(Long userId);
    
    /**
     * 更新用户健康档案
     */
    void updateHealthProfile(Long userId, UpdateHealthProfileDTO dto);
}
