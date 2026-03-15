package com.recipe.srback.service;

import com.recipe.srback.dto.SaveFoodCheckinDTO;
import com.recipe.srback.vo.DietDiaryVO;
import com.recipe.srback.vo.TodayCheckinVO;

import java.time.LocalDate;
import java.util.List;

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
    
    /**
     * 查询指定日期的饮食日记
     * 
     * @param userId 用户ID
     * @param date 日期
     * @return 饮食日记
     */
    DietDiaryVO getDietDiaryByDate(Long userId, LocalDate date);
    
    /**
     * 查询日期范围内的饮食日记列表
     * 
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 饮食日记列表
     */
    List<DietDiaryVO> getDietDiaryList(Long userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * 删除饮食日记
     * 
     * @param userId 用户ID
     * @param diaryId 日记ID
     */
    void deleteDietDiary(Long userId, Long diaryId);
}
