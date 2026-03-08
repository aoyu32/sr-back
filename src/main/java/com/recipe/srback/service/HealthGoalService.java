package com.recipe.srback.service;

import com.recipe.srback.dto.AddHealthGoalDTO;
import com.recipe.srback.dto.CompleteHealthGoalDTO;
import com.recipe.srback.dto.UpdateHealthGoalDTO;
import com.recipe.srback.vo.HealthGoalVO;

import java.util.List;

/**
 * 健康目标服务接口
 */
public interface HealthGoalService {
    
    /**
     * 添加健康目标
     */
    void addHealthGoal(Long userId, AddHealthGoalDTO dto);
    
    /**
     * 查询当前健康目标
     */
    HealthGoalVO getCurrentHealthGoal(Long userId);
    
    /**
     * 更新健康目标
     */
    void updateHealthGoal(Long userId, Long goalId, UpdateHealthGoalDTO dto);
    
    /**
     * 取消健康目标
     */
    void cancelHealthGoal(Long userId, Long goalId);
    
    /**
     * 完成健康目标
     */
    void completeHealthGoal(Long userId, Long goalId, CompleteHealthGoalDTO dto);
    
    /**
     * 查询历史目标列表
     */
    List<HealthGoalVO> getHistoryHealthGoals(Long userId);
    
    /**
     * 删除历史目标
     */
    void deleteHistoryGoal(Long userId, Long goalId);
}
