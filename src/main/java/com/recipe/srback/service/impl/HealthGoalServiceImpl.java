package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recipe.srback.dto.AddHealthGoalDTO;
import com.recipe.srback.dto.CompleteHealthGoalDTO;
import com.recipe.srback.dto.UpdateHealthGoalDTO;
import com.recipe.srback.entity.UserHealthGoal;
import com.recipe.srback.enums.HealthGoalTypeEnum;
import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.mapper.UserHealthGoalMapper;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.service.HealthGoalService;
import com.recipe.srback.vo.HealthGoalVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 健康目标服务实现类
 */
@Slf4j
@Service
public class HealthGoalServiceImpl implements HealthGoalService {
    
    @Autowired
    private UserHealthGoalMapper healthGoalMapper;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addHealthGoal(Long userId, AddHealthGoalDTO dto) {
        // 检查是否已有进行中的目标
        LambdaQueryWrapper<UserHealthGoal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserHealthGoal::getUserId, userId);
        wrapper.eq(UserHealthGoal::getStatus, "active");
        Long count = healthGoalMapper.selectCount(wrapper);
        
        if (count > 0) {
            throw new BusinessException(ResultCodeEnum.BAD_REQUEST.getCode(), "已有进行中的目标，请先完成或取消");
        }
        
        UserHealthGoal goal = new UserHealthGoal();
        goal.setUserId(userId);
        goal.setGoalType(dto.getGoalType());
        goal.setTargetWeight(dto.getTargetWeight());
        goal.setTargetBmi(dto.getTargetBmi());
        goal.setTargetMuscle(dto.getTargetMuscle());
        goal.setTargetBloodSugar(dto.getTargetBloodSugar());
        goal.setTargetBloodPressure(dto.getTargetBloodPressure());
        goal.setDailyCalories(dto.getDailyCalories());
        goal.setDailyProtein(dto.getDailyProtein());
        goal.setDailyCarbs(dto.getDailyCarbs());
        goal.setDailySodium(dto.getDailySodium());
        goal.setStartDate(LocalDate.now());
        
        if (dto.getEndDate() != null && !dto.getEndDate().isEmpty()) {
            goal.setEndDate(LocalDate.parse(dto.getEndDate(), DATE_FORMATTER));
        }
        
        goal.setStatus("active");
        
        healthGoalMapper.insert(goal);
    }
    
    @Override
    public HealthGoalVO getCurrentHealthGoal(Long userId) {
        LambdaQueryWrapper<UserHealthGoal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserHealthGoal::getUserId, userId);
        wrapper.eq(UserHealthGoal::getStatus, "active");
        wrapper.orderByDesc(UserHealthGoal::getCreatedAt);
        wrapper.last("LIMIT 1");
        
        UserHealthGoal goal = healthGoalMapper.selectOne(wrapper);
        
        if (goal == null) {
            return null;
        }
        
        return convertToVO(goal);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHealthGoal(Long userId, Long goalId, UpdateHealthGoalDTO dto) {
        UserHealthGoal goal = healthGoalMapper.selectById(goalId);
        
        if (goal == null || !goal.getUserId().equals(userId)) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        
        if (!"active".equals(goal.getStatus())) {
            throw new BusinessException(ResultCodeEnum.BAD_REQUEST.getCode(), "只能修改进行中的目标");
        }
        
        // 使用 UpdateWrapper 来确保 null 值也会被更新
        goal.setGoalType(dto.getGoalType());
        goal.setTargetWeight(dto.getTargetWeight());
        goal.setTargetBmi(dto.getTargetBmi());
        goal.setTargetMuscle(dto.getTargetMuscle());
        goal.setTargetBloodSugar(dto.getTargetBloodSugar());
        goal.setTargetBloodPressure(dto.getTargetBloodPressure());
        goal.setDailyCalories(dto.getDailyCalories());
        goal.setDailyProtein(dto.getDailyProtein());
        goal.setDailyCarbs(dto.getDailyCarbs());
        goal.setDailySodium(dto.getDailySodium());
        
        if (dto.getEndDate() != null && !dto.getEndDate().isEmpty()) {
            goal.setEndDate(LocalDate.parse(dto.getEndDate(), DATE_FORMATTER));
        } else {
            goal.setEndDate(null);
        }
        
        // 使用 alwaysUpdateSomeColumnById 或者直接在 entity 上配置
        healthGoalMapper.updateById(goal);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelHealthGoal(Long userId, Long goalId) {
        UserHealthGoal goal = healthGoalMapper.selectById(goalId);
        
        if (goal == null || !goal.getUserId().equals(userId)) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        
        if (!"active".equals(goal.getStatus())) {
            throw new BusinessException(ResultCodeEnum.BAD_REQUEST.getCode(), "只能取消进行中的目标");
        }
        
        goal.setStatus("cancelled");
        healthGoalMapper.updateById(goal);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeHealthGoal(Long userId, Long goalId, CompleteHealthGoalDTO dto) {
        UserHealthGoal goal = healthGoalMapper.selectById(goalId);
        
        if (goal == null || !goal.getUserId().equals(userId)) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        
        if (!"active".equals(goal.getStatus())) {
            throw new BusinessException(ResultCodeEnum.BAD_REQUEST.getCode(), "只能完成进行中的目标");
        }
        
        goal.setStatus("completed");
        goal.setResult(dto.getResult());
        healthGoalMapper.updateById(goal);
    }
    
    @Override
    public List<HealthGoalVO> getHistoryHealthGoals(Long userId) {
        LambdaQueryWrapper<UserHealthGoal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserHealthGoal::getUserId, userId);
        wrapper.in(UserHealthGoal::getStatus, "completed", "cancelled");
        wrapper.orderByDesc(UserHealthGoal::getCreatedAt);
        
        List<UserHealthGoal> goals = healthGoalMapper.selectList(wrapper);
        
        return goals.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHistoryGoal(Long userId, Long goalId) {
        UserHealthGoal goal = healthGoalMapper.selectById(goalId);
        
        if (goal == null || !goal.getUserId().equals(userId)) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        
        if ("active".equals(goal.getStatus())) {
            throw new BusinessException(ResultCodeEnum.BAD_REQUEST.getCode(), "不能删除进行中的目标");
        }
        
        healthGoalMapper.deleteById(goalId);
    }
    
    /**
     * 转换为VO
     */
    private HealthGoalVO convertToVO(UserHealthGoal goal) {
        HealthGoalVO vo = new HealthGoalVO();
        vo.setId(goal.getId());
        vo.setGoalType(goal.getGoalType());
        vo.setTarget(HealthGoalTypeEnum.getNameByCode(goal.getGoalType()));
        vo.setTargetWeight(goal.getTargetWeight());
        vo.setTargetBMI(goal.getTargetBmi());
        vo.setTargetMuscle(goal.getTargetMuscle());
        vo.setTargetBloodSugar(goal.getTargetBloodSugar());
        vo.setTargetBloodPressure(goal.getTargetBloodPressure());
        vo.setDailyCalories(goal.getDailyCalories());
        vo.setDailyProtein(goal.getDailyProtein());
        vo.setDailyCarbs(goal.getDailyCarbs());
        vo.setDailySodium(goal.getDailySodium());
        vo.setStartDate(goal.getStartDate() != null ? goal.getStartDate().format(DATE_FORMATTER) : null);
        vo.setEndDate(goal.getEndDate() != null ? goal.getEndDate().format(DATE_FORMATTER) : null);
        vo.setStatus(goal.getStatus());
        vo.setResult(goal.getResult());
        
        return vo;
    }
}
