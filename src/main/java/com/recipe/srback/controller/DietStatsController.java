package com.recipe.srback.controller;

import com.recipe.srback.result.Result;
import com.recipe.srback.service.DietStatsService;
import com.recipe.srback.vo.DietStatsOverviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 饮食数据统计控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/diet-stats")
@RequiredArgsConstructor
@Tag(name = "饮食数据统计", description = "饮食数据统计相关接口")
public class DietStatsController {

    private final DietStatsService dietStatsService;

    /**
     * 获取饮食统计总览
     *
     * @param userId 用户ID
     * @param days 统计天数，支持7或30
     * @return 统计总览
     */
    @GetMapping("/overview")
    @Operation(summary = "获取饮食统计总览")
    public Result<DietStatsOverviewVO> getOverview(@RequestAttribute("userId") Long userId,
                                                   @RequestParam(defaultValue = "7") Integer days) {
        log.info("获取饮食统计总览，用户ID：{}，统计天数：{}", userId, days);
        return Result.success(dietStatsService.getOverview(userId, days));
    }
}
