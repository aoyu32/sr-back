package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recipe.srback.entity.DietDiary;
import com.recipe.srback.entity.DietDiaryMeal;
import com.recipe.srback.mapper.DietDiaryMapper;
import com.recipe.srback.mapper.DietDiaryMealMapper;
import com.recipe.srback.service.DietStatsService;
import com.recipe.srback.vo.DietStatsOverviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 饮食数据统计服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DietStatsServiceImpl implements DietStatsService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");

    private final DietDiaryMapper dietDiaryMapper;
    private final DietDiaryMealMapper dietDiaryMealMapper;

    @Override
    public DietStatsOverviewVO getOverview(Long userId, Integer days) {
        int rangeDays = normalizeDays(days);
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(rangeDays - 1L);

        List<DietDiary> diaries = loadDiaries(userId, startDate, endDate);
        Map<LocalDate, DietDiary> diaryMap = diaries.stream()
                .collect(Collectors.toMap(
                        DietDiary::getDiaryDate,
                        item -> item,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        List<Long> diaryIds = diaries.stream()
                .map(DietDiary::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<DietDiaryMeal> meals = CollectionUtils.isEmpty(diaryIds)
                ? List.of()
                : loadMeals(diaryIds);

        Map<String, MealAccumulator> mealAccumulatorMap = createMealAccumulatorMap();
        for (DietDiaryMeal meal : meals) {
            if (!Objects.equals(meal.getIsChecked(), 1)) {
                continue;
            }

            MealAccumulator accumulator = mealAccumulatorMap.get(normalizeMealType(meal.getMealType()));
            if (accumulator == null) {
                continue;
            }

            int calories = meal.getMealCalories() != null ? meal.getMealCalories() : 0;
            accumulator.totalCalories += calories;
            accumulator.checkedCount += 1;
        }

        List<DietStatsOverviewVO.TrendVO> caloriesTrend = new ArrayList<>();
        List<DietStatsOverviewVO.TrendVO> checkinTrend = new ArrayList<>();

        int totalCalories = 0;
        int checkedMealsCount = 0;
        int checkinDays = 0;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DietDiary diary = diaryMap.get(date);
            int dayCalories = diary != null && diary.getTotalCalories() != null ? diary.getTotalCalories() : 0;
            int dayCheckins = diary != null && diary.getMealsCheckedCount() != null ? diary.getMealsCheckedCount() : 0;

            totalCalories += dayCalories;
            checkedMealsCount += dayCheckins;
            if (dayCheckins > 0) {
                checkinDays += 1;
            }

            caloriesTrend.add(buildTrendVO(date, dayCalories));
            checkinTrend.add(buildTrendVO(date, dayCheckins));
        }

        DietStatsOverviewVO.SummaryVO summaryVO = new DietStatsOverviewVO.SummaryVO();
        summaryVO.setTotalCalories(totalCalories);
        summaryVO.setAvgDailyCalories(roundOneDecimal((double) totalCalories / rangeDays));
        summaryVO.setCheckinDays(checkinDays);
        summaryVO.setCheckedMealsCount(checkedMealsCount);
        summaryVO.setCheckinRate(roundOneDecimal((double) checkinDays * 100 / rangeDays));

        List<DietStatsOverviewVO.MealAverageVO> mealAverageList = buildMealAverageList(mealAccumulatorMap);

        DietStatsOverviewVO vo = new DietStatsOverviewVO();
        vo.setRangeDays(rangeDays);
        vo.setStartDate(startDate.toString());
        vo.setEndDate(endDate.toString());
        vo.setRangeLabel("近" + rangeDays + "天");
        vo.setSummary(summaryVO);
        vo.setCaloriesTrend(caloriesTrend);
        vo.setCheckinTrend(checkinTrend);
        vo.setMealAverageList(mealAverageList);

        return vo;
    }

    private int normalizeDays(Integer days) {
        if (days == null) {
            return 7;
        }
        return days >= 30 ? 30 : 7;
    }

    private List<DietDiary> loadDiaries(Long userId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<DietDiary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DietDiary::getUserId, userId)
                .ge(DietDiary::getDiaryDate, startDate)
                .le(DietDiary::getDiaryDate, endDate)
                .orderByAsc(DietDiary::getDiaryDate);
        return dietDiaryMapper.selectList(wrapper);
    }

    private List<DietDiaryMeal> loadMeals(List<Long> diaryIds) {
        LambdaQueryWrapper<DietDiaryMeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(DietDiaryMeal::getDiaryId, diaryIds)
                .orderByAsc(DietDiaryMeal::getDiaryId)
                .orderByAsc(DietDiaryMeal::getMealType);
        return dietDiaryMealMapper.selectList(wrapper);
    }

    private DietStatsOverviewVO.TrendVO buildTrendVO(LocalDate date, int value) {
        DietStatsOverviewVO.TrendVO vo = new DietStatsOverviewVO.TrendVO();
        vo.setDate(date.toString());
        vo.setLabel(date.format(DATE_FORMATTER));
        vo.setValue(value);
        return vo;
    }

    private List<DietStatsOverviewVO.MealAverageVO> buildMealAverageList(Map<String, MealAccumulator> mealAccumulatorMap) {
        List<DietStatsOverviewVO.MealAverageVO> list = new ArrayList<>();
        list.add(buildMealAverageVO("breakfast", "早餐", mealAccumulatorMap.get("breakfast")));
        list.add(buildMealAverageVO("lunch", "午餐", mealAccumulatorMap.get("lunch")));
        list.add(buildMealAverageVO("dinner", "晚餐", mealAccumulatorMap.get("dinner")));
        return list;
    }

    private DietStatsOverviewVO.MealAverageVO buildMealAverageVO(String mealType, String mealName, MealAccumulator accumulator) {
        DietStatsOverviewVO.MealAverageVO vo = new DietStatsOverviewVO.MealAverageVO();
        vo.setMealType(mealType);
        vo.setMealName(mealName);
        vo.setTotalCalories(accumulator.totalCalories);
        vo.setCheckedCount(accumulator.checkedCount);
        vo.setAverageCalories(accumulator.checkedCount == 0 ? 0.0 : roundOneDecimal((double) accumulator.totalCalories / accumulator.checkedCount));
        return vo;
    }

    private Map<String, MealAccumulator> createMealAccumulatorMap() {
        Map<String, MealAccumulator> map = new LinkedHashMap<>();
        map.put("breakfast", new MealAccumulator());
        map.put("lunch", new MealAccumulator());
        map.put("dinner", new MealAccumulator());
        return map;
    }

    private String normalizeMealType(String mealType) {
        if ("breakfast".equals(mealType) || "lunch".equals(mealType) || "dinner".equals(mealType)) {
            return mealType;
        }
        return null;
    }

    private double roundOneDecimal(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    private static class MealAccumulator {
        private int totalCalories = 0;
        private int checkedCount = 0;
    }
}
