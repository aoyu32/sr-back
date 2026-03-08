package com.recipe.srback.controller;

import com.recipe.srback.dto.AddHealthGoalDTO;
import com.recipe.srback.dto.CompleteHealthGoalDTO;
import com.recipe.srback.dto.UpdateHealthGoalDTO;
import com.recipe.srback.result.Result;
import com.recipe.srback.service.HealthGoalService;
import com.recipe.srback.vo.HealthGoalVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 健康目标控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/health-goal")
public class HealthGoalController {
    
    @Autowired
    private HealthGoalService healthGoalService;
    
    /**
     * 添加健康目标
     */
    @PostMapping
    public Result<Void> addHealthGoal(@RequestBody AddHealthGoalDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("添加健康目标，用户ID：{}，数据：{}", userId, dto);
        
        healthGoalService.addHealthGoal(userId, dto);
        return Result.success();
    }
    
    /**
     * 查询当前健康目标
     */
    @GetMapping("/current")
    public Result<HealthGoalVO> getCurrentHealthGoal(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询当前健康目标，用户ID：{}", userId);
        
        HealthGoalVO goal = healthGoalService.getCurrentHealthGoal(userId);
        return Result.success(goal);
    }
    
    /**
     * 更新健康目标
     */
    @PutMapping("/{goalId}")
    public Result<Void> updateHealthGoal(@PathVariable Long goalId, 
                                         @RequestBody UpdateHealthGoalDTO dto, 
                                         HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("更新健康目标，用户ID：{}，目标ID：{}，数据：{}", userId, goalId, dto);
        
        healthGoalService.updateHealthGoal(userId, goalId, dto);
        return Result.success();
    }
    
    /**
     * 取消健康目标
     */
    @PutMapping("/{goalId}/cancel")
    public Result<Void> cancelHealthGoal(@PathVariable Long goalId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("取消健康目标，用户ID：{}，目标ID：{}", userId, goalId);
        
        healthGoalService.cancelHealthGoal(userId, goalId);
        return Result.success();
    }
    
    /**
     * 完成健康目标
     */
    @PutMapping("/{goalId}/complete")
    public Result<Void> completeHealthGoal(@PathVariable Long goalId, 
                                           @RequestBody CompleteHealthGoalDTO dto, 
                                           HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("完成健康目标，用户ID：{}，目标ID：{}，结果：{}", userId, goalId, dto.getResult());
        
        healthGoalService.completeHealthGoal(userId, goalId, dto);
        return Result.success();
    }
    
    /**
     * 查询历史目标列表
     */
    @GetMapping("/history")
    public Result<List<HealthGoalVO>> getHistoryHealthGoals(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询历史目标列表，用户ID：{}", userId);
        
        List<HealthGoalVO> goals = healthGoalService.getHistoryHealthGoals(userId);
        return Result.success(goals);
    }
    
    /**
     * 删除历史目标
     */
    @DeleteMapping("/{goalId}")
    public Result<Void> deleteHistoryGoal(@PathVariable Long goalId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("删除历史目标，用户ID：{}，目标ID：{}", userId, goalId);
        
        healthGoalService.deleteHistoryGoal(userId, goalId);
        return Result.success();
    }
}
