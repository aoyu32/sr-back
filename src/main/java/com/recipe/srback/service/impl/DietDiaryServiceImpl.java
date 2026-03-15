package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recipe.srback.dto.SaveFoodCheckinDTO;
import com.recipe.srback.entity.DietDiary;
import com.recipe.srback.entity.DietDiaryFood;
import com.recipe.srback.entity.DietDiaryMeal;
import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.mapper.DietDiaryFoodMapper;
import com.recipe.srback.mapper.DietDiaryMapper;
import com.recipe.srback.mapper.DietDiaryMealMapper;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.service.DietDiaryService;
import com.recipe.srback.vo.TodayCheckinVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 饮食日记服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DietDiaryServiceImpl implements DietDiaryService {
    
    private final DietDiaryMapper dietDiaryMapper;
    private final DietDiaryMealMapper dietDiaryMealMapper;
    private final DietDiaryFoodMapper dietDiaryFoodMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveFoodCheckin(Long userId, SaveFoodCheckinDTO dto) {
        LocalDate today = LocalDate.now();
        
        // 1. 获取或创建今日饮食日记
        DietDiary diary = getOrCreateDiary(userId, today);
        
        // 2. 获取或创建餐次记录
        DietDiaryMeal meal = getOrCreateMeal(diary.getId(), dto.getMealType());
        
        // 3. 创建食物记录
        DietDiaryFood food = new DietDiaryFood();
        food.setMealId(meal.getId());
        food.setFoodImage(dto.getFoodImage());
        food.setFoodName(dto.getFoodName());
        food.setCalories(dto.getCalories());
        food.setProtein(BigDecimal.valueOf(dto.getProtein()));
        food.setCarbs(BigDecimal.valueOf(dto.getCarbs()));
        food.setFat(BigDecimal.valueOf(dto.getFat()));
        food.setAmount(dto.getAmount());
        food.setAiConfidence(BigDecimal.valueOf(dto.getConfidence()));
        food.setIsManual(0);
        
        // 获取当前餐次的食物数量，作为排序
        LambdaQueryWrapper<DietDiaryFood> foodQuery = new LambdaQueryWrapper<>();
        foodQuery.eq(DietDiaryFood::getMealId, meal.getId());
        long foodCount = dietDiaryFoodMapper.selectCount(foodQuery);
        food.setSortOrder((int) foodCount);
        
        dietDiaryFoodMapper.insert(food);
        
        // 4. 更新餐次营养统计
        updateMealNutrition(meal.getId());
        
        // 5. 更新日记营养统计
        updateDiaryNutrition(diary.getId());
        
        log.info("保存食物打卡成功，用户：{}，餐次：{}，食物：{}", userId, dto.getMealType(), dto.getFoodName());
        
        return food.getId();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFoodCheckin(Long userId, Long foodId) {
        // 1. 查询食物记录
        DietDiaryFood food = dietDiaryFoodMapper.selectById(foodId);
        if (food == null) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        
        // 2. 查询餐次记录
        DietDiaryMeal meal = dietDiaryMealMapper.selectById(food.getMealId());
        if (meal == null) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        
        // 3. 查询日记记录，验证用户权限
        DietDiary diary = dietDiaryMapper.selectById(meal.getDiaryId());
        if (diary == null || !diary.getUserId().equals(userId)) {
            throw new BusinessException(ResultCodeEnum.NO_PERMISSION);
        }
        
        // 4. 删除食物记录
        dietDiaryFoodMapper.deleteById(foodId);
        
        // 5. 更新餐次营养统计
        updateMealNutrition(meal.getId());
        
        // 6. 更新日记营养统计
        updateDiaryNutrition(diary.getId());
        
        log.info("删除食物打卡成功，用户：{}，食物ID：{}", userId, foodId);
    }
    
    /**
     * 获取或创建今日饮食日记
     */
    private DietDiary getOrCreateDiary(Long userId, LocalDate date) {
        LambdaQueryWrapper<DietDiary> query = new LambdaQueryWrapper<>();
        query.eq(DietDiary::getUserId, userId)
             .eq(DietDiary::getDiaryDate, date);
        
        DietDiary diary = dietDiaryMapper.selectOne(query);
        
        if (diary == null) {
            diary = new DietDiary();
            diary.setUserId(userId);
            diary.setDiaryDate(date);
            diary.setTotalCalories(0);
            diary.setTotalProtein(BigDecimal.ZERO);
            diary.setTotalCarbs(BigDecimal.ZERO);
            diary.setTotalFat(BigDecimal.ZERO);
            diary.setMealsCheckedCount(0);
            
            dietDiaryMapper.insert(diary);
        }
        
        return diary;
    }
    
    /**
     * 获取或创建餐次记录
     */
    private DietDiaryMeal getOrCreateMeal(Long diaryId, String mealType) {
        LambdaQueryWrapper<DietDiaryMeal> query = new LambdaQueryWrapper<>();
        query.eq(DietDiaryMeal::getDiaryId, diaryId)
             .eq(DietDiaryMeal::getMealType, mealType);
        
        DietDiaryMeal meal = dietDiaryMealMapper.selectOne(query);
        
        if (meal == null) {
            meal = new DietDiaryMeal();
            meal.setDiaryId(diaryId);
            meal.setMealType(mealType);
            meal.setIsChecked(0);
            meal.setMealCalories(0);
            meal.setMealProtein(BigDecimal.ZERO);
            meal.setMealCarbs(BigDecimal.ZERO);
            meal.setMealFat(BigDecimal.ZERO);
            
            dietDiaryMealMapper.insert(meal);
        }
        
        return meal;
    }
    
    /**
     * 更新餐次营养统计
     */
    private void updateMealNutrition(Long mealId) {
        // 查询该餐次的所有食物
        LambdaQueryWrapper<DietDiaryFood> query = new LambdaQueryWrapper<>();
        query.eq(DietDiaryFood::getMealId, mealId);
        List<DietDiaryFood> foods = dietDiaryFoodMapper.selectList(query);
        
        // 计算总营养
        int totalCalories = foods.stream()
                .mapToInt(f -> f.getCalories() != null ? f.getCalories() : 0)
                .sum();
        
        BigDecimal totalProtein = foods.stream()
                .map(f -> f.getProtein() != null ? f.getProtein() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCarbs = foods.stream()
                .map(f -> f.getCarbs() != null ? f.getCarbs() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalFat = foods.stream()
                .map(f -> f.getFat() != null ? f.getFat() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 更新餐次记录
        DietDiaryMeal meal = new DietDiaryMeal();
        meal.setId(mealId);
        meal.setMealCalories(totalCalories);
        meal.setMealProtein(totalProtein);
        meal.setMealCarbs(totalCarbs);
        meal.setMealFat(totalFat);
        meal.setIsChecked(foods.isEmpty() ? 0 : 1);
        meal.setCheckTime(foods.isEmpty() ? null : LocalDateTime.now());
        
        dietDiaryMealMapper.updateById(meal);
    }
    
    /**
     * 更新日记营养统计
     */
    private void updateDiaryNutrition(Long diaryId) {
        // 查询该日记的所有餐次
        LambdaQueryWrapper<DietDiaryMeal> query = new LambdaQueryWrapper<>();
        query.eq(DietDiaryMeal::getDiaryId, diaryId);
        List<DietDiaryMeal> meals = dietDiaryMealMapper.selectList(query);
        
        // 计算总营养
        int totalCalories = meals.stream()
                .mapToInt(m -> m.getMealCalories() != null ? m.getMealCalories() : 0)
                .sum();
        
        BigDecimal totalProtein = meals.stream()
                .map(m -> m.getMealProtein() != null ? m.getMealProtein() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCarbs = meals.stream()
                .map(m -> m.getMealCarbs() != null ? m.getMealCarbs() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalFat = meals.stream()
                .map(m -> m.getMealFat() != null ? m.getMealFat() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int checkedCount = (int) meals.stream()
                .filter(m -> m.getIsChecked() != null && m.getIsChecked() == 1)
                .count();
        
        // 更新日记记录
        DietDiary diary = new DietDiary();
        diary.setId(diaryId);
        diary.setTotalCalories(totalCalories);
        diary.setTotalProtein(totalProtein);
        diary.setTotalCarbs(totalCarbs);
        diary.setTotalFat(totalFat);
        diary.setMealsCheckedCount(checkedCount);
        
        dietDiaryMapper.updateById(diary);
    }
    
    @Override
    public TodayCheckinVO getTodayCheckin(Long userId) {
        LocalDate today = LocalDate.now();
        
        // 查询今日饮食日记
        LambdaQueryWrapper<DietDiary> diaryQuery = new LambdaQueryWrapper<>();
        diaryQuery.eq(DietDiary::getUserId, userId)
                  .eq(DietDiary::getDiaryDate, today);
        DietDiary diary = dietDiaryMapper.selectOne(diaryQuery);
        
        TodayCheckinVO vo = new TodayCheckinVO();
        List<TodayCheckinVO.MealCheckinVO> meals = new ArrayList<>();
        
        // 定义三餐类型
        String[] mealTypes = {"breakfast", "lunch", "dinner"};
        String[] mealLabels = {"早餐", "午餐", "晚餐"};
        
        for (int i = 0; i < mealTypes.length; i++) {
            String mealType = mealTypes[i];
            String mealLabel = mealLabels[i];
            
            TodayCheckinVO.MealCheckinVO mealVO = new TodayCheckinVO.MealCheckinVO();
            mealVO.setMealType(mealType);
            mealVO.setLabel(mealLabel);
            mealVO.setChecked(false);
            mealVO.setCalories(0);
            mealVO.setFoods(new ArrayList<>());
            
            // 如果有日记记录，查询餐次和食物
            if (diary != null) {
                LambdaQueryWrapper<DietDiaryMeal> mealQuery = new LambdaQueryWrapper<>();
                mealQuery.eq(DietDiaryMeal::getDiaryId, diary.getId())
                         .eq(DietDiaryMeal::getMealType, mealType);
                DietDiaryMeal meal = dietDiaryMealMapper.selectOne(mealQuery);
                
                if (meal != null && meal.getIsChecked() == 1) {
                    mealVO.setChecked(true);
                    mealVO.setCalories(meal.getMealCalories());
                    
                    // 查询食物列表
                    LambdaQueryWrapper<DietDiaryFood> foodQuery = new LambdaQueryWrapper<>();
                    foodQuery.eq(DietDiaryFood::getMealId, meal.getId())
                             .orderByAsc(DietDiaryFood::getSortOrder);
                    List<DietDiaryFood> foods = dietDiaryFoodMapper.selectList(foodQuery);
                    
                    // 转换为VO
                    List<TodayCheckinVO.FoodItemVO> foodVOs = foods.stream()
                            .map(food -> {
                                TodayCheckinVO.FoodItemVO foodVO = new TodayCheckinVO.FoodItemVO();
                                foodVO.setId(food.getId());
                                foodVO.setFoodImage(food.getFoodImage());
                                foodVO.setFoodName(food.getFoodName());
                                foodVO.setCalories(food.getCalories());
                                foodVO.setProtein(food.getProtein() != null ? food.getProtein().doubleValue() : 0.0);
                                foodVO.setCarbs(food.getCarbs() != null ? food.getCarbs().doubleValue() : 0.0);
                                foodVO.setFat(food.getFat() != null ? food.getFat().doubleValue() : 0.0);
                                foodVO.setAmount(food.getAmount());
                                foodVO.setConfidence(food.getAiConfidence() != null ? food.getAiConfidence().doubleValue() : 0.0);
                                return foodVO;
                            })
                            .collect(Collectors.toList());
                    
                    mealVO.setFoods(foodVOs);
                }
            }
            
            meals.add(mealVO);
        }
        
        vo.setMeals(meals);
        
        log.info("查询用户{}今日打卡记录，共{}餐", userId, 
                meals.stream().filter(TodayCheckinVO.MealCheckinVO::getChecked).count());
        
        return vo;
    }
}
