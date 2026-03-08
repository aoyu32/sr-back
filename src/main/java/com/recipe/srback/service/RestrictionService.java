package com.recipe.srback.service;

import com.recipe.srback.dto.AddRestrictionDTO;
import com.recipe.srback.vo.RestrictionVO;

import java.util.List;

/**
 * 特殊禁忌服务接口
 */
public interface RestrictionService {
    /**
     * 添加特殊禁忌
     */
    void addRestriction(Long userId, AddRestrictionDTO dto);
    
    /**
     * 获取用户的所有特殊禁忌
     */
    List<RestrictionVO> getUserRestrictions(Long userId);
    
    /**
     * 删除特殊禁忌
     */
    void deleteRestriction(Long userId, Long restrictionId);
}
