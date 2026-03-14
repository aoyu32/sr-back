package com.recipe.srback.controller;

import com.recipe.srback.result.Result;
import com.recipe.srback.service.DailyRecipeService;
import com.recipe.srback.utils.JwtUtil;
import com.recipe.srback.vo.DailyRecipeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 每日食谱推荐控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI服务", description = "AI智能推荐相关接口")
public class DailyRecipeController {
    
    private final DailyRecipeService dailyRecipeService;
    private final JwtUtil jwtUtil;
    
    /**
     * 生成每日食谱推荐
     */
    @PostMapping("/daily-recipe")
    @Operation(summary = "生成每日食谱推荐", description = "根据用户健康档案生成个性化每日食谱")
    public Result<DailyRecipeVO> generateDailyRecipe(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "给我推荐今日食谱") String input) {
        try {
            // 从token中获取用户ID
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            log.info("用户{}请求生成每日食谱，input：{}", userId, input);
            
            // 调用服务生成食谱
            DailyRecipeVO result = dailyRecipeService.generateDailyRecipe(userId, input);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("生成每日食谱失败", e);
            return Result.error("生成每日食谱失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取今日食谱推荐（优先从数据库查询）
     */
    @PostMapping("/daily-recipe/today")
    @Operation(summary = "获取今日食谱推荐", description = "优先从数据库查询今日食谱，不存在则自动生成")
    public Result<DailyRecipeVO> getTodayRecipe(HttpServletRequest request) {
        try {
            // 从token中获取用户ID
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            log.info("用户{}请求获取今日食谱", userId);
            
            // 调用服务获取今日食谱
            DailyRecipeVO result = dailyRecipeService.getTodayRecipe(userId);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取今日食谱失败", e);
            return Result.error("获取今日食谱失败：" + e.getMessage());
        }
    }
}
