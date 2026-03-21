package com.recipe.srback.service;

import com.recipe.srback.vo.DietStatsOverviewVO;

/**
 * 饮食数据统计服务
 */
public interface DietStatsService {

    /**
     * 获取统计总览
     *
     * @param userId 用户ID
     * @param days 统计天数
     * @return 统计总览
     */
    DietStatsOverviewVO getOverview(Long userId, Integer days);
}
