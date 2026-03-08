package com.recipe.srback.controller;

import com.recipe.srback.result.Result;
import com.recipe.srback.service.MyContentService;
import com.recipe.srback.utils.JwtUtil;
import com.recipe.srback.vo.MyContentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 我的内容统计控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/my-content")
public class MyContentController {
    
    @Autowired
    private MyContentService myContentService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取我的内容统计
     */
    @GetMapping("/stats")
    public Result<MyContentVO> getMyContentStats(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        // 移除 "Bearer " 前缀
        String token = authorization.replace("Bearer ", "");
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        MyContentVO stats = myContentService.getMyContentStats(userId);
        
        return Result.success(stats);
    }
}
