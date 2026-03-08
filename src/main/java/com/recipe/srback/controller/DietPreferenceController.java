package com.recipe.srback.controller;

import com.recipe.srback.dto.AddDietPreferenceDTO;
import com.recipe.srback.dto.UpdateDietPreferenceDTO;
import com.recipe.srback.result.Result;
import com.recipe.srback.service.DietPreferenceService;
import com.recipe.srback.vo.DietPreferenceVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 饮食偏好控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/diet-preference")
public class DietPreferenceController {
    
    @Autowired
    private DietPreferenceService preferenceService;
    
    /**
     * 添加饮食偏好
     */
    @PostMapping("/add")
    public Result<Void> addPreference(@RequestAttribute("userId") Long userId,
                                      @RequestBody AddDietPreferenceDTO dto) {
        log.info("添加饮食偏好，userId: {}, dto: {}", userId, dto);
        preferenceService.addPreference(userId, dto);
        return Result.success();
    }
    
    /**
     * 获取用户的所有饮食偏好
     */
    @GetMapping("/list")
    public Result<List<DietPreferenceVO>> getUserPreferences(@RequestAttribute("userId") Long userId) {
        log.info("获取用户饮食偏好列表，userId: {}", userId);
        List<DietPreferenceVO> preferences = preferenceService.getUserPreferences(userId);
        return Result.success(preferences);
    }
    
    /**
     * 更新饮食偏好
     */
    @PutMapping("/update/{preferenceId}")
    public Result<Void> updatePreference(@RequestAttribute("userId") Long userId,
                                         @PathVariable Long preferenceId,
                                         @RequestBody UpdateDietPreferenceDTO dto) {
        log.info("更新饮食偏好，userId: {}, preferenceId: {}, dto: {}", userId, preferenceId, dto);
        preferenceService.updatePreference(userId, preferenceId, dto);
        return Result.success();
    }
    
    /**
     * 删除饮食偏好
     */
    @DeleteMapping("/delete/{preferenceId}")
    public Result<Void> deletePreference(@RequestAttribute("userId") Long userId,
                                         @PathVariable Long preferenceId) {
        log.info("删除饮食偏好，userId: {}, preferenceId: {}", userId, preferenceId);
        preferenceService.deletePreference(userId, preferenceId);
        return Result.success();
    }
}
