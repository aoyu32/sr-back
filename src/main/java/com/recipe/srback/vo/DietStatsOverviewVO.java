package com.recipe.srback.vo;

import lombok.Data;

import java.util.List;

/**
 * 饮食数据统计总览VO
 */
@Data
public class DietStatsOverviewVO {

    /**
     * 统计天数
     */
    private Integer rangeDays;

    /**
     * 统计起始日期
     */
    private String startDate;

    /**
     * 统计结束日期
     */
    private String endDate;

    /**
     * 时间范围文案
     */
    private String rangeLabel;

    /**
     * 汇总数据
     */
    private SummaryVO summary;

    /**
     * 每日热量趋势
     */
    private List<TrendVO> caloriesTrend;

    /**
     * 每日打卡趋势
     */
    private List<TrendVO> checkinTrend;

    /**
     * 每餐平均热量
     */
    private List<MealAverageVO> mealAverageList;

    @Data
    public static class SummaryVO {
        private Integer totalCalories;
        private Double avgDailyCalories;
        private Integer checkinDays;
        private Integer checkedMealsCount;
        private Double checkinRate;
    }

    @Data
    public static class TrendVO {
        private String date;
        private String label;
        private Integer value;
    }

    @Data
    public static class MealAverageVO {
        private String mealType;
        private String mealName;
        private Integer totalCalories;
        private Integer checkedCount;
        private Double averageCalories;
    }
}
