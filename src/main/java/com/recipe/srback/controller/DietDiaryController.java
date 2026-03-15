package com.recipe.srback.controller;

import com.recipe.srback.result.Result;
import com.recipe.srback.service.DietDiaryService;
import com.recipe.srback.utils.JwtUtil;
import com.recipe.srback.vo.DietDiaryVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 饮食日记控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/diet-diary")
@RequiredArgsConstructor
public class DietDiaryController {
    
    private final DietDiaryService dietDiaryService;
    private final JwtUtil jwtUtil;
    
    /**
     * 查询指定日期的饮食日记
     * 
     * @param request HTTP请求
     * @param date 日期（YYYY-MM-DD）
     * @return 饮食日记
     */
    @GetMapping("/{date}")
    public Result<DietDiaryVO> getDietDiaryByDate(
            HttpServletRequest request,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        
        // 获取用户ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        log.info("用户{}查询{}的饮食日记", userId, date);
        
        // 调用服务查询
        DietDiaryVO vo = dietDiaryService.getDietDiaryByDate(userId, date);
        
        return Result.success(vo);
    }
    
    /**
     * 查询日期范围内的饮食日记列表
     * 
     * @param request HTTP请求
     * @param startDate 开始日期（YYYY-MM-DD）
     * @param endDate 结束日期（YYYY-MM-DD）
     * @return 饮食日记列表
     */
    @GetMapping("/list")
    public Result<List<DietDiaryVO>> getDietDiaryList(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        
        // 获取用户ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        log.info("用户{}查询饮食日记列表，日期范围：{} - {}", userId, startDate, endDate);
        
        // 调用服务查询
        List<DietDiaryVO> list = dietDiaryService.getDietDiaryList(userId, startDate, endDate);
        
        return Result.success(list);
    }
    
    /**
     * 删除饮食日记
     * 
     * @param request HTTP请求
     * @param diaryId 日记ID
     * @return 成功标识
     */
    @DeleteMapping("/{diaryId}")
    public Result<Void> deleteDietDiary(
            HttpServletRequest request,
            @PathVariable Long diaryId) {
        
        // 获取用户ID
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        log.info("用户{}删除饮食日记，日记ID：{}", userId, diaryId);
        
        // 调用服务删除
        dietDiaryService.deleteDietDiary(userId, diaryId);
        
        return Result.success();
    }
}
