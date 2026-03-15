package com.recipe.srback.controller;

import com.recipe.srback.dto.RecipeAnalysisRequestDTO;
import com.recipe.srback.result.Result;
import com.recipe.srback.service.RecipeAnalysisService;
import com.recipe.srback.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 食谱分析控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI服务", description = "AI智能分析相关接口")
public class RecipeAnalysisController {
    
    private final RecipeAnalysisService recipeAnalysisService;
    private final JwtUtil jwtUtil;
    
    /**
     * 分析食谱（流式返回）
     */
    @PostMapping(value = "/recipe-analysis/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "分析食谱（流式）", description = "上传食物图片，AI流式返回营养分析和建议")
    public SseEmitter analyzeRecipeStream(
            HttpServletRequest request,
            @RequestBody RecipeAnalysisRequestDTO dto) {
        
        // 从token中获取用户ID（可选，用于日志记录）
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Long userId = jwtUtil.getUserIdFromToken(token);
            log.info("用户{}请求食谱分析，imageUrl：{}", userId, dto.getImageUrl());
        } catch (Exception e) {
            log.warn("解析token失败，继续处理请求", e);
        }
        
        // 调用服务进行流式分析
        return recipeAnalysisService.analyzeRecipeStream(dto.getImageUrl(), dto.getInput());
    }
}
