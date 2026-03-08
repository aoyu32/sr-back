package com.recipe.srback.controller;

import com.recipe.srback.dto.UpdateHealthProfileDTO;
import com.recipe.srback.result.Result;
import com.recipe.srback.service.HealthProfileService;
import com.recipe.srback.vo.HealthProfileVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 健康档案控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/health-profile")
public class HealthProfileController {
    
    @Autowired
    private HealthProfileService healthProfileService;
    
    /**
     * 查询健康档案
     */
    @GetMapping
    public Result<HealthProfileVO> getHealthProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("查询健康档案，用户ID：{}", userId);
        
        HealthProfileVO profile = healthProfileService.getHealthProfile(userId);
        return Result.success(profile);
    }
    
    /**
     * 更新健康档案
     */
    @PutMapping
    public Result<Void> updateHealthProfile(@RequestBody UpdateHealthProfileDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        log.info("更新健康档案，用户ID：{}，数据：{}", userId, dto);
        
        healthProfileService.updateHealthProfile(userId, dto);
        return Result.success();
    }
}
