package com.recipe.srback.service;

import com.recipe.srback.dto.SaveFoodCheckinDTO;
import com.recipe.srback.vo.TodayCheckinVO;

/**
 * 饮食日记服务
 */
public interface DietDiaryService {
    
    /**
     * 保存食物打卡记录
     * 
     * @param userId 用户ID
     * @param dto 打卡数据
     * @return 食物记录ID
     */
    Long saveFoodCheckin(Long userId, SaveFoodCheckinDTO dto);
    
    /**
     * 删除食物打卡记录
     * 
     * @param userId 用户ID
     * @param foodId 食物记录ID
     */
    void deleteFoodCheckin(Long userId, Long foodId);
    
    /**
     * 查询今日打卡记录
     * 
     * @param userId 用户ID
     * @return 今日打卡记录
     */
    TodayCheckinVO getTodayCheckin(Long userId);
}
