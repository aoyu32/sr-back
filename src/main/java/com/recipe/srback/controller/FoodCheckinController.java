package com.recipe.srback.controller;

import com.recipe.srback.dto.FoodCheckinRequestDTO;
import com.recipe.srback.dto.SaveFoodCheckinDTO;
import com.recipe.srback.result.Result;
import com.recipe.srback.service.DietDiaryService;
import com.recipe.srback.service.FoodCheckinService;
import com.recipe.srback.utils.JwtUtil;
import com.recipe.srback.vo.FoodCheckinVO;
import com.recipe.srback.vo.TodayCheckinVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 食物打卡控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class FoodCheckinController {
    
    private final FoodCheckinService foodCheckinService;
    private final DietDiaryService dietDiaryService;
    private final com.recipe.srback.service.DailyRecipeService dailyRecipeService;
    private final JwtUtil jwtUtil;
    
    /**
     * 分析食物图片（饮食打卡）
     * 
     * @param request HTTP请求
     * @param dto 请求参数
     * @return 食物营养数据
     */
    @PostMapping("/food-checkin")
    public Result<FoodCheckinVO> analyzeFoodImage(
            HttpServletRequest request,
            @RequestBody FoodCheckinRequestDTO dto) {
        
        // 获取用户ID（从token中解析）
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        log.info("用户{}请求食物打卡分析，imageUrl：{}，mealType：{}", 
                userId, dto.getImageUrl(), dto.getMealType());
        
        // 调用服务
        FoodCheckinVO vo = foodCheckinService.analyzeFoodImage(
                dto.getImageUrl(), 
                dto.getMealType()
        );
        
        return Result.success(vo);
    }
    
    /**
     * 保存食物打卡记录
     * 
     * @param request HTTP请求
     * @param dto 打卡数据
     * @return 食物记录ID
     */
    @PostMapping("/food-checkin/save")
    public Result<Long> saveFoodCheckin(
            HttpServletRequest request,
            @RequestBody SaveFoodCheckinDTO dto) {
        
        // 获取用户ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        log.info("用户{}保存食物打卡，餐次：{}，食物：{}", 
                userId, dto.getMealType(), dto.getFoodName());
        
        // 调用服务保存
        Long foodId = dietDiaryService.saveFoodCheckin(userId, dto);
        
        return Result.success(foodId);
    }
    
    /**
     * 删除食物打卡记录
     * 
     * @param request HTTP请求
     * @param foodId 食物记录ID
     * @return 成功标识
     */
    @DeleteMapping("/food-checkin/{foodId}")
    public Result<Void> deleteFoodCheckin(
            HttpServletRequest request,
            @PathVariable Long foodId) {
        
        // 获取用户ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        log.info("用户{}删除食物打卡，食物ID：{}", userId, foodId);
        
        // 调用服务删除
        dietDiaryService.deleteFoodCheckin(userId, foodId);
        
        return Result.success();
    }
    
    /**
     * 查询今日打卡记录
     * 
     * @param request HTTP请求
     * @return 今日打卡记录
     */
    @GetMapping("/food-checkin/today")
    public Result<TodayCheckinVO> getTodayCheckin(HttpServletRequest request) {
        // 获取用户ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        log.info("用户{}查询今日打卡记录", userId);
        
        // 调用服务查询
        TodayCheckinVO vo = dietDiaryService.getTodayCheckin(userId);
        
        return Result.success(vo);
    }
    
    /**
     * 将食谱添加到今日餐食
     * 
     * @param request HTTP请求
     * @param dto 请求参数
     * @return 食谱项ID
     */
    @PostMapping("/food-checkin/add-recipe")
    public Result<Long> addRecipeToMeal(
            HttpServletRequest request,
            @RequestBody com.recipe.srback.dto.AddRecipeToMealDTO dto) {
        
        // 获取用户ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        log.info("用户{}添加食谱到今日食谱，食谱ID：{}，餐次：{}", 
                userId, dto.getRecipeId(), dto.getMealType());
        
        // 调用服务添加到今日食谱推荐
        Long itemId = dailyRecipeService.addRecipeToTodayPlan(userId, dto.getRecipeId(), dto.getMealType());
        
        return Result.success(itemId);
    }
}
