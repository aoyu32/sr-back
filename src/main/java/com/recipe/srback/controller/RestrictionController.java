package com.recipe.srback.controller;

import com.recipe.srback.dto.AddRestrictionDTO;
import com.recipe.srback.result.Result;
import com.recipe.srback.service.RestrictionService;
import com.recipe.srback.vo.RestrictionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 特殊禁忌控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/restriction")
public class RestrictionController {
    
    @Autowired
    private RestrictionService restrictionService;
    
    /**
     * 添加特殊禁忌
     */
    @PostMapping("/add")
    public Result<Void> addRestriction(@RequestAttribute("userId") Long userId,
                                       @RequestBody AddRestrictionDTO dto) {
        log.info("添加特殊禁忌，userId: {}, dto: {}", userId, dto);
        restrictionService.addRestriction(userId, dto);
        return Result.success();
    }
    
    /**
     * 获取用户的所有特殊禁忌
     */
    @GetMapping("/list")
    public Result<List<RestrictionVO>> getUserRestrictions(@RequestAttribute("userId") Long userId) {
        log.info("获取用户特殊禁忌列表，userId: {}", userId);
        List<RestrictionVO> restrictions = restrictionService.getUserRestrictions(userId);
        return Result.success(restrictions);
    }
    
    /**
     * 删除特殊禁忌
     */
    @DeleteMapping("/delete/{restrictionId}")
    public Result<Void> deleteRestriction(@RequestAttribute("userId") Long userId,
                                          @PathVariable Long restrictionId) {
        log.info("删除特殊禁忌，userId: {}, restrictionId: {}", userId, restrictionId);
        restrictionService.deleteRestriction(userId, restrictionId);
        return Result.success();
    }
}
