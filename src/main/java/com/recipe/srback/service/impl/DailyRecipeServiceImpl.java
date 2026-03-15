package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe.srback.config.CozeConfig;
import com.recipe.srback.dto.DailyRecipeRequestDTO;
import com.recipe.srback.entity.UserDietPreference;
import com.recipe.srback.entity.UserHealthGoal;
import com.recipe.srback.entity.UserRestriction;
import com.recipe.srback.entity.DailyRecipePlan;
import com.recipe.srback.entity.DailyRecipePlanMeal;
import com.recipe.srback.entity.DailyRecipePlanMealItem;
import com.recipe.srback.entity.Recipe;
import com.recipe.srback.entity.RecipeCategory;
import com.recipe.srback.mapper.UserDietPreferenceMapper;
import com.recipe.srback.mapper.UserHealthGoalMapper;
import com.recipe.srback.mapper.UserRestrictionMapper;
import com.recipe.srback.mapper.DailyRecipePlanMapper;
import com.recipe.srback.mapper.DailyRecipePlanMealMapper;
import com.recipe.srback.mapper.DailyRecipePlanMealItemMapper;
import com.recipe.srback.mapper.RecipeMapper;
import com.recipe.srback.mapper.RecipeCategoryMapper;
import com.recipe.srback.service.DailyRecipeService;
import com.recipe.srback.vo.DailyRecipeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 每日食谱推荐服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DailyRecipeServiceImpl implements DailyRecipeService {
    
    private final UserHealthGoalMapper healthGoalMapper;
    private final UserRestrictionMapper restrictionMapper;
    private final UserDietPreferenceMapper dietPreferenceMapper;
    private final DailyRecipePlanMapper dailyRecipePlanMapper;
    private final DailyRecipePlanMealMapper dailyRecipePlanMealMapper;
    private final DailyRecipePlanMealItemMapper dailyRecipePlanMealItemMapper;
    private final RecipeMapper recipeMapper;
    private final RecipeCategoryMapper recipeCategoryMapper;
    private final CozeConfig cozeConfig;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    
    @Override
    public DailyRecipeVO generateDailyRecipe(Long userId, String input) {
        LocalDate today = LocalDate.now();
        
        // 0. 查询今日旧的激活计划
        LambdaQueryWrapper<DailyRecipePlan> oldPlanWrapper = new LambdaQueryWrapper<>();
        oldPlanWrapper.eq(DailyRecipePlan::getUserId, userId)
                      .eq(DailyRecipePlan::getPlanDate, today)
                      .eq(DailyRecipePlan::getIsActive, 1);
        List<DailyRecipePlan> oldPlans = dailyRecipePlanMapper.selectList(oldPlanWrapper);
        
        // 删除旧计划的关联数据（meal 和 meal_item）
        for (DailyRecipePlan oldPlan : oldPlans) {
            Long oldPlanId = oldPlan.getId();
            
            // 查询该计划的所有餐次
            LambdaQueryWrapper<DailyRecipePlanMeal> mealWrapper = new LambdaQueryWrapper<>();
            mealWrapper.eq(DailyRecipePlanMeal::getPlanId, oldPlanId);
            List<DailyRecipePlanMeal> meals = dailyRecipePlanMealMapper.selectList(mealWrapper);
            
            // 删除每个餐次的食谱项
            for (DailyRecipePlanMeal meal : meals) {
                LambdaQueryWrapper<DailyRecipePlanMealItem> itemWrapper = new LambdaQueryWrapper<>();
                itemWrapper.eq(DailyRecipePlanMealItem::getMealPlanId, meal.getId());
                dailyRecipePlanMealItemMapper.delete(itemWrapper);
            }
            
            // 删除餐次
            dailyRecipePlanMealMapper.delete(mealWrapper);
            
            // 标记计划为不激活
            oldPlan.setIsActive(0);
            oldPlan.setUpdatedAt(LocalDateTime.now());
            dailyRecipePlanMapper.updateById(oldPlan);
            
            log.info("删除旧计划的关联数据，planId：{}", oldPlanId);
        }
        
        // 1. 查询用户健康目标
        DailyRecipeRequestDTO.HealthGoalDTO healthGoal = getUserHealthGoal(userId);
        
        // 2. 查询用户特殊禁忌
        List<DailyRecipeRequestDTO.RestrictionDTO> restrictions = getUserRestrictions(userId);
        
        // 3. 查询用户饮食偏好
        List<DailyRecipeRequestDTO.DietPreferenceDTO> preferences = getUserDietPreferences(userId);
        
        // 4. 构建请求参数（使用自定义input）
        DailyRecipeRequestDTO requestDTO = buildRequest(healthGoal, restrictions, preferences, input);
        
        // 5. 调用Coze工作流
        DailyRecipeVO recipeVO = callCozeWorkflow(requestDTO);
        
        // 6. 检查返回结果，只有成功时才保存到数据库
        if (recipeVO != null && recipeVO.getMeals() != null && !recipeVO.getMeals().isEmpty()) {
            try {
                saveDailyRecipe(userId, recipeVO);
                log.info("AI生成食谱成功并保存，用户ID：{}", userId);
            } catch (Exception e) {
                log.error("保存每日食谱失败，但不影响返回结果", e);
            }
        } else {
            log.warn("Coze工作流返回数据为空，不保存到数据库，用户ID：{}", userId);
            throw new RuntimeException("AI生成食谱失败，请稍后重试");
        }
        
        return recipeVO;
    }
    
    /**
     * 查询用户健康目标（包含所有字段）
     */
    private DailyRecipeRequestDTO.HealthGoalDTO getUserHealthGoal(Long userId) {
        LambdaQueryWrapper<UserHealthGoal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserHealthGoal::getUserId, userId)
               .eq(UserHealthGoal::getStatus, "active")
               .orderByDesc(UserHealthGoal::getCreatedAt)
               .last("LIMIT 1");
        
        UserHealthGoal healthGoal = healthGoalMapper.selectOne(wrapper);
        
        if (healthGoal == null) {
            return null;
        }
        
        DailyRecipeRequestDTO.HealthGoalDTO dto = new DailyRecipeRequestDTO.HealthGoalDTO();
        dto.setId(healthGoal.getId());
        dto.setGoalType(healthGoal.getGoalType());
        dto.setTargetWeight(healthGoal.getTargetWeight() != null ? healthGoal.getTargetWeight().doubleValue() : null);
        dto.setTargetBmi(healthGoal.getTargetBmi() != null ? healthGoal.getTargetBmi().doubleValue() : null);
        dto.setTargetMuscle(healthGoal.getTargetMuscle() != null ? healthGoal.getTargetMuscle().doubleValue() : null);
        dto.setTargetBloodSugar(healthGoal.getTargetBloodSugar() != null ? healthGoal.getTargetBloodSugar().doubleValue() : null);
        dto.setTargetBloodPressure(healthGoal.getTargetBloodPressure());
        dto.setDailyCalories(healthGoal.getDailyCalories());
        dto.setDailyProtein(healthGoal.getDailyProtein() != null ? healthGoal.getDailyProtein().doubleValue() : null);
        dto.setDailyCarbs(healthGoal.getDailyCarbs() != null ? healthGoal.getDailyCarbs().doubleValue() : null);
        dto.setDailySodium(healthGoal.getDailySodium() != null ? healthGoal.getDailySodium().doubleValue() : null);
        dto.setStartDate(healthGoal.getStartDate() != null ? healthGoal.getStartDate().toString() : null);
        dto.setEndDate(healthGoal.getEndDate() != null ? healthGoal.getEndDate().toString() : null);
        dto.setStatus(healthGoal.getStatus());
        
        return dto;
    }
    
    /**
     * 查询用户特殊禁忌
     */
    private List<DailyRecipeRequestDTO.RestrictionDTO> getUserRestrictions(Long userId) {
        LambdaQueryWrapper<UserRestriction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRestriction::getUserId, userId);
        
        List<UserRestriction> restrictions = restrictionMapper.selectList(wrapper);
        
        return restrictions.stream().map(r -> {
            DailyRecipeRequestDTO.RestrictionDTO dto = new DailyRecipeRequestDTO.RestrictionDTO();
            dto.setId(r.getId());
            dto.setType(r.getType());
            dto.setName(r.getName());
            dto.setDescription(r.getDescription());
            dto.setSeverity(r.getSeverity());
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 查询用户饮食偏好
     */
    private List<DailyRecipeRequestDTO.DietPreferenceDTO> getUserDietPreferences(Long userId) {
        LambdaQueryWrapper<UserDietPreference> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDietPreference::getUserId, userId);
        
        List<UserDietPreference> preferences = dietPreferenceMapper.selectList(wrapper);
        
        return preferences.stream().map(p -> {
            DailyRecipeRequestDTO.DietPreferenceDTO dto = new DailyRecipeRequestDTO.DietPreferenceDTO();
            dto.setId(p.getId());
            dto.setPreferenceType(p.getPreferenceType());
            dto.setFoodName(p.getFoodName());
            return dto;
        }).collect(Collectors.toList());
    }
    
    /**
     * 构建请求参数
     */
    private DailyRecipeRequestDTO buildRequest(
            DailyRecipeRequestDTO.HealthGoalDTO healthGoal,
            List<DailyRecipeRequestDTO.RestrictionDTO> restrictions,
            List<DailyRecipeRequestDTO.DietPreferenceDTO> preferences,
            String input) {
        
        DailyRecipeRequestDTO request = new DailyRecipeRequestDTO();
        
        // 设置当前日期时间
        String currentDateTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        request.setDate(currentDateTime);
        
        // 设置用户输入的提示（如果为空则使用默认值）
        request.setInput(input != null && !input.trim().isEmpty() ? input : "给我推荐今日食谱");
        
        // 设置健康目标
        request.setHealthGoal(healthGoal);
        
        // 设置特殊禁忌
        request.setRestriction(restrictions != null ? restrictions : new ArrayList<>());
        
        // 设置饮食偏好
        request.setDietPreference(preferences != null ? preferences : new ArrayList<>());
        
        return request;
    }
    
    /**
     * 调用Coze工作流
     */
    private DailyRecipeVO callCozeWorkflow(DailyRecipeRequestDTO requestDTO) {
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("workflow_id", cozeConfig.getWorkflowId());
            requestBody.put("parameters", requestDTO);
            
            String requestJson = objectMapper.writeValueAsString(requestBody);
            log.info("调用Coze工作流，请求参数：{}", requestJson);
            
            // 创建WebClient
            WebClient webClient = webClientBuilder
                    .baseUrl(cozeConfig.getBaseUrl())
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + cozeConfig.getAccessToken())
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();
            
            // 调用非流式API，等待完整响应（使用配置的超时时间）
            String responseStr = webClient.post()
                    .uri("/v1/workflow/run")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(java.time.Duration.ofMillis(cozeConfig.getTimeout()))
                    .block();
            
            log.info("Coze工作流响应：{}", responseStr);
            
            if (responseStr == null || responseStr.isEmpty()) {
                throw new RuntimeException("Coze工作流响应为空");
            }
            
            // 解析JSON响应
            JsonNode jsonResponse = objectMapper.readTree(responseStr);
            
            // 检查响应码
            int code = jsonResponse.get("code").asInt();
            if (code != 0) {
                String msg = jsonResponse.get("msg").asText();
                throw new RuntimeException("Coze API调用失败：" + msg);
            }
            
            // 提取data字段
            JsonNode dataNode = jsonResponse.get("data");
            if (dataNode == null) {
                throw new RuntimeException("Coze响应中没有data字段");
            }
            
            log.info("Coze data字段：{}", dataNode.toString());
            
            // 判断data是字符串还是对象
            JsonNode outputNode;
            if (dataNode.isTextual()) {
                // data是字符串，需要解析
                String dataStr = dataNode.asText();
                JsonNode parsedData = objectMapper.readTree(dataStr);
                
                // 提取output字段
                JsonNode outputField = parsedData.get("output");
                if (outputField == null) {
                    throw new RuntimeException("Coze响应中没有output字段");
                }
                
                // 判断output是字符串还是对象
                if (outputField.isTextual()) {
                    String outputStr = outputField.asText();
                    log.info("Coze output字段（字符串）：{}", outputStr);
                    outputNode = objectMapper.readTree(outputStr);
                } else {
                    log.info("Coze output字段（对象）：{}", outputField.toString());
                    outputNode = outputField;
                }
            } else {
                // data是对象，直接提取output
                JsonNode outputField = dataNode.get("output");
                if (outputField == null) {
                    throw new RuntimeException("Coze响应中没有output字段");
                }
                
                log.info("Coze output字段：{}", outputField.toString());
                outputNode = outputField;
            }
            
            // 转换为VO
            return objectMapper.treeToValue(outputNode, DailyRecipeVO.class);
            
        } catch (Exception e) {
            log.error("调用Coze工作流失败", e);
            throw new RuntimeException("生成每日食谱失败：" + e.getMessage());
        }
    }
    
    @Override
    public DailyRecipeVO getTodayRecipe(Long userId) {
        // 1. 查询今日是否已有食谱
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<DailyRecipePlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DailyRecipePlan::getUserId, userId)
               .eq(DailyRecipePlan::getPlanDate, today)
               .eq(DailyRecipePlan::getIsActive, 1)
               .orderByDesc(DailyRecipePlan::getCreatedAt)
               .last("LIMIT 1");
        
        DailyRecipePlan existingPlan = dailyRecipePlanMapper.selectOne(wrapper);
        
        // 2. 如果存在，直接返回
        if (existingPlan != null) {
            log.info("用户{}今日已有食谱计划，直接返回", userId);
            return convertPlanToVO(existingPlan);
        }
        
        // 3. 不存在则生成新的
        log.info("用户{}今日无食谱计划，开始生成", userId);
        return generateDailyRecipe(userId, "给我推荐今日食谱");
    }
    
    /**
     * 保存每日食谱到数据库（使用三表结构）
     */
    private void saveDailyRecipe(Long userId, DailyRecipeVO recipeVO) {
        try {
            // 1. 插入主表 daily_recipe_plan
            DailyRecipePlan plan = new DailyRecipePlan();
            plan.setUserId(userId);
            plan.setPlanDate(LocalDate.now());
            plan.setTitle("Today，我的食谱");
            plan.setDescription(recipeVO.getDescription());
            plan.setTotalCalories(recipeVO.getTotalCalories());
            plan.setTotalProtein(recipeVO.getTotalProtein() != null ? 
                    BigDecimal.valueOf(recipeVO.getTotalProtein()) : BigDecimal.ZERO);
            plan.setTotalCarbs(recipeVO.getTotalCarbs() != null ? 
                    BigDecimal.valueOf(recipeVO.getTotalCarbs()) : BigDecimal.ZERO);
            plan.setTotalFat(recipeVO.getTotalFat() != null ? 
                    BigDecimal.valueOf(recipeVO.getTotalFat()) : BigDecimal.ZERO);
            plan.setGenerationType("ai");
            plan.setIsActive(1);
            plan.setCreatedAt(LocalDateTime.now());
            plan.setUpdatedAt(LocalDateTime.now());
            
            dailyRecipePlanMapper.insert(plan);
            Long planId = plan.getId();
            log.info("保存每日食谱计划成功，planId：{}", planId);
            
            // 2. 遍历三餐，插入 daily_recipe_plan_meal 和 daily_recipe_plan_meal_item
            Map<String, DailyRecipeVO.MealVO> meals = recipeVO.getMeals();
            if (meals != null) {
                saveMeal(planId, "breakfast", meals.get("breakfast"), 1);
                saveMeal(planId, "lunch", meals.get("lunch"), 2);
                saveMeal(planId, "dinner", meals.get("dinner"), 3);
            }
            
            log.info("保存每日食谱成功，用户ID：{}", userId);
        } catch (Exception e) {
            log.error("保存每日食谱失败", e);
            throw new RuntimeException("保存每日食谱失败：" + e.getMessage());
        }
    }
    
    /**
     * 保存单个餐次
     */
    private void saveMeal(Long planId, String mealType, DailyRecipeVO.MealVO mealVO, int sortOrder) {
        if (mealVO == null) {
            return;
        }
        
        // 1. 插入餐次表
        DailyRecipePlanMeal meal = new DailyRecipePlanMeal();
        meal.setPlanId(planId);
        meal.setMealType(mealType);
        meal.setMealName(getMealName(mealType));
        meal.setTimeRange(mealVO.getTimeRange());
        meal.setSortOrder(sortOrder);
        meal.setCreatedAt(LocalDateTime.now());
        meal.setUpdatedAt(LocalDateTime.now());
        
        dailyRecipePlanMealMapper.insert(meal);
        Long mealPlanId = meal.getId();
        log.info("保存餐次成功，mealType：{}，mealPlanId：{}", mealType, mealPlanId);
        
        // 2. 插入餐次食谱项
        List<DailyRecipeVO.RecipeItemVO> recipes = mealVO.getRecipes();
        if (recipes != null) {
            for (int i = 0; i < recipes.size(); i++) {
                DailyRecipeVO.RecipeItemVO recipeItem = recipes.get(i);
                saveMealItem(mealPlanId, recipeItem, i + 1);
            }
        }
    }
    
    /**
     * 保存单个食谱项
     */
    private void saveMealItem(Long mealPlanId, DailyRecipeVO.RecipeItemVO recipeItemVO, int sortOrder) {
        // 查询食谱详情
        Recipe recipe = recipeMapper.selectById(recipeItemVO.getRecipeId());
        if (recipe == null) {
            log.warn("食谱不存在，recipeId：{}", recipeItemVO.getRecipeId());
            return;
        }
        
        DailyRecipePlanMealItem item = new DailyRecipePlanMealItem();
        item.setMealPlanId(mealPlanId);
        item.setRecipeId(recipe.getId());
        item.setItemName(recipe.getName());
        item.setItemImage(recipe.getImage());
        item.setAmount(recipeItemVO.getAmount());
        item.setCalories(recipe.getCalories());
        item.setProtein(recipe.getProtein());
        item.setCarbs(recipe.getCarbs());
        item.setFat(recipe.getFat());
        item.setSortOrder(sortOrder);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        
        dailyRecipePlanMealItemMapper.insert(item);
        log.info("保存食谱项成功，recipeId：{}，itemName：{}", recipe.getId(), recipe.getName());
    }
    
    /**
     * 获取餐次名称
     */
    private String getMealName(String mealType) {
        switch (mealType) {
            case "breakfast":
                return "早餐";
            case "lunch":
                return "午餐";
            case "dinner":
                return "晚餐";
            default:
                return mealType;
        }
    }
    
    /**
     * 将数据库记录转换为VO（从三表结构查询并丰富数据）
     */
    private DailyRecipeVO convertPlanToVO(DailyRecipePlan plan) {
        try {
            DailyRecipeVO vo = new DailyRecipeVO();
            vo.setType("daily_recipe");
            vo.setDescription(plan.getDescription());
            vo.setTotalCalories(plan.getTotalCalories());
            vo.setTotalProtein(plan.getTotalProtein() != null ? plan.getTotalProtein().doubleValue() : null);
            vo.setTotalCarbs(plan.getTotalCarbs() != null ? plan.getTotalCarbs().doubleValue() : null);
            vo.setTotalFat(plan.getTotalFat() != null ? plan.getTotalFat().doubleValue() : null);
            
            // 查询餐次数据
            LambdaQueryWrapper<DailyRecipePlanMeal> mealWrapper = new LambdaQueryWrapper<>();
            mealWrapper.eq(DailyRecipePlanMeal::getPlanId, plan.getId())
                       .orderByAsc(DailyRecipePlanMeal::getSortOrder);
            List<DailyRecipePlanMeal> meals = dailyRecipePlanMealMapper.selectList(mealWrapper);
            
            // 构建meals Map
            Map<String, DailyRecipeVO.MealVO> mealsMap = new HashMap<>();
            for (DailyRecipePlanMeal meal : meals) {
                DailyRecipeVO.MealVO mealVO = new DailyRecipeVO.MealVO();
                mealVO.setTimeRange(meal.getTimeRange());
                
                // 查询该餐次的食谱项
                LambdaQueryWrapper<DailyRecipePlanMealItem> itemWrapper = new LambdaQueryWrapper<>();
                itemWrapper.eq(DailyRecipePlanMealItem::getMealPlanId, meal.getId())
                           .orderByAsc(DailyRecipePlanMealItem::getSortOrder);
                List<DailyRecipePlanMealItem> items = dailyRecipePlanMealItemMapper.selectList(itemWrapper);
                
                // 计算餐次总营养
                int mealCalories = 0;
                double mealProtein = 0.0;
                double mealCarbs = 0.0;
                double mealFat = 0.0;
                
                // 转换为RecipeItemVO并计算总营养
                List<DailyRecipeVO.RecipeItemVO> recipeItems = new ArrayList<>();
                for (DailyRecipePlanMealItem item : items) {
                    DailyRecipeVO.RecipeItemVO itemVO = new DailyRecipeVO.RecipeItemVO();
                    itemVO.setItemId(item.getId());
                    itemVO.setRecipeId(item.getRecipeId());
                    itemVO.setRecipeName(item.getItemName());
                    itemVO.setAmount(item.getAmount());
                    itemVO.setImage(item.getItemImage());
                    itemVO.setCalories(item.getCalories());
                    itemVO.setProtein(item.getProtein() != null ? item.getProtein().doubleValue() : null);
                    itemVO.setCarbs(item.getCarbs() != null ? item.getCarbs().doubleValue() : null);
                    itemVO.setFat(item.getFat() != null ? item.getFat().doubleValue() : null);
                    
                    // 查询分类名称
                    Recipe recipe = recipeMapper.selectById(item.getRecipeId());
                    if (recipe != null && recipe.getCategoryId() != null) {
                        RecipeCategory category = recipeCategoryMapper.selectById(recipe.getCategoryId());
                        if (category != null) {
                            itemVO.setCategory(category.getName());
                        }
                    }
                    
                    recipeItems.add(itemVO);
                    
                    // 累加营养值
                    mealCalories += item.getCalories();
                    if (item.getProtein() != null) {
                        mealProtein += item.getProtein().doubleValue();
                    }
                    if (item.getCarbs() != null) {
                        mealCarbs += item.getCarbs().doubleValue();
                    }
                    if (item.getFat() != null) {
                        mealFat += item.getFat().doubleValue();
                    }
                }
                
                mealVO.setRecipes(recipeItems);
                mealVO.setMealCalories(mealCalories);
                mealVO.setMealProtein(mealProtein);
                mealVO.setMealCarbs(mealCarbs);
                mealVO.setMealFat(mealFat);
                
                mealsMap.put(meal.getMealType(), mealVO);
            }
            
            vo.setMeals(mealsMap);
            
            return vo;
        } catch (Exception e) {
            log.error("转换食谱数据失败", e);
            throw new RuntimeException("转换食谱数据失败：" + e.getMessage());
        }
    }
    
    @Override
    public Long addRecipeToTodayPlan(Long userId, Long recipeId, String mealType) {
        LocalDate today = LocalDate.now();
        
        // 1. 查询食谱信息
        Recipe recipe = recipeMapper.selectById(recipeId);
        if (recipe == null) {
            throw new RuntimeException("食谱不存在");
        }
        
        // 2. 查询或创建今日食谱计划
        LambdaQueryWrapper<DailyRecipePlan> planWrapper = new LambdaQueryWrapper<>();
        planWrapper.eq(DailyRecipePlan::getUserId, userId)
                   .eq(DailyRecipePlan::getPlanDate, today)
                   .eq(DailyRecipePlan::getIsActive, 1)
                   .orderByDesc(DailyRecipePlan::getCreatedAt)
                   .last("LIMIT 1");
        
        DailyRecipePlan plan = dailyRecipePlanMapper.selectOne(planWrapper);
        
        // 如果没有今日计划，创建一个空计划
        if (plan == null) {
            plan = new DailyRecipePlan();
            plan.setUserId(userId);
            plan.setPlanDate(today);
            plan.setTitle("Today，我的食谱");
            plan.setDescription("手动添加的食谱");
            plan.setTotalCalories(0);
            plan.setTotalProtein(BigDecimal.ZERO);
            plan.setTotalCarbs(BigDecimal.ZERO);
            plan.setTotalFat(BigDecimal.ZERO);
            plan.setGenerationType("manual");
            plan.setIsActive(1);
            plan.setCreatedAt(LocalDateTime.now());
            plan.setUpdatedAt(LocalDateTime.now());
            
            dailyRecipePlanMapper.insert(plan);
            log.info("创建今日食谱计划，planId：{}", plan.getId());
        }
        
        Long planId = plan.getId();
        
        // 3. 查询或创建餐次记录
        LambdaQueryWrapper<DailyRecipePlanMeal> mealWrapper = new LambdaQueryWrapper<>();
        mealWrapper.eq(DailyRecipePlanMeal::getPlanId, planId)
                   .eq(DailyRecipePlanMeal::getMealType, mealType);
        
        DailyRecipePlanMeal meal = dailyRecipePlanMealMapper.selectOne(mealWrapper);
        
        if (meal == null) {
            meal = new DailyRecipePlanMeal();
            meal.setPlanId(planId);
            meal.setMealType(mealType);
            meal.setMealName(getMealName(mealType));
            meal.setTimeRange(getDefaultTimeRange(mealType));
            meal.setSortOrder(getSortOrder(mealType));
            meal.setCreatedAt(LocalDateTime.now());
            meal.setUpdatedAt(LocalDateTime.now());
            
            dailyRecipePlanMealMapper.insert(meal);
            log.info("创建餐次记录，mealType：{}，mealId：{}", mealType, meal.getId());
        }
        
        Long mealPlanId = meal.getId();
        
        // 4. 检查该食谱是否已经添加到该餐次（防止重复）
        LambdaQueryWrapper<DailyRecipePlanMealItem> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(DailyRecipePlanMealItem::getMealPlanId, mealPlanId)
                    .eq(DailyRecipePlanMealItem::getRecipeId, recipeId);
        Long existCount = dailyRecipePlanMealItemMapper.selectCount(checkWrapper);
        
        if (existCount > 0) {
            throw new RuntimeException("该食谱已添加到今日" + getMealName(mealType));
        }
        
        // 5. 获取当前餐次的食谱数量，作为排序
        LambdaQueryWrapper<DailyRecipePlanMealItem> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(DailyRecipePlanMealItem::getMealPlanId, mealPlanId);
        long itemCount = dailyRecipePlanMealItemMapper.selectCount(countWrapper);
        
        // 6. 创建食谱项记录
        DailyRecipePlanMealItem item = new DailyRecipePlanMealItem();
        item.setMealPlanId(mealPlanId);
        item.setRecipeId(recipe.getId());
        item.setItemName(recipe.getName());
        item.setItemImage(recipe.getImage());
        item.setAmount("1份");
        item.setCalories(recipe.getCalories());
        item.setProtein(recipe.getProtein());
        item.setCarbs(recipe.getCarbs());
        item.setFat(recipe.getFat());
        item.setSortOrder((int) itemCount + 1);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        
        dailyRecipePlanMealItemMapper.insert(item);
        
        // 7. 更新计划的总营养值
        updatePlanNutrition(planId);
        
        log.info("添加食谱到今日食谱成功，用户：{}，食谱：{}，餐次：{}", userId, recipe.getName(), mealType);
        
        return item.getId();
    }
    
    /**
     * 获取默认时间范围
     */
    private String getDefaultTimeRange(String mealType) {
        switch (mealType) {
            case "breakfast":
                return "07:00-09:00";
            case "lunch":
                return "11:30-13:00";
            case "dinner":
                return "18:00-20:00";
            default:
                return "00:00-00:00";
        }
    }
    
    /**
     * 获取排序值
     */
    private int getSortOrder(String mealType) {
        switch (mealType) {
            case "breakfast":
                return 1;
            case "lunch":
                return 2;
            case "dinner":
                return 3;
            default:
                return 99;
        }
    }
    
    /**
     * 更新计划的总营养值
     */
    private void updatePlanNutrition(Long planId) {
        // 查询该计划的所有餐次
        LambdaQueryWrapper<DailyRecipePlanMeal> mealWrapper = new LambdaQueryWrapper<>();
        mealWrapper.eq(DailyRecipePlanMeal::getPlanId, planId);
        List<DailyRecipePlanMeal> meals = dailyRecipePlanMealMapper.selectList(mealWrapper);
        
        int totalCalories = 0;
        BigDecimal totalProtein = BigDecimal.ZERO;
        BigDecimal totalCarbs = BigDecimal.ZERO;
        BigDecimal totalFat = BigDecimal.ZERO;
        
        // 遍历每个餐次，累加所有食谱项的营养值
        for (DailyRecipePlanMeal meal : meals) {
            LambdaQueryWrapper<DailyRecipePlanMealItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(DailyRecipePlanMealItem::getMealPlanId, meal.getId());
            List<DailyRecipePlanMealItem> items = dailyRecipePlanMealItemMapper.selectList(itemWrapper);
            
            for (DailyRecipePlanMealItem item : items) {
                totalCalories += item.getCalories() != null ? item.getCalories() : 0;
                totalProtein = totalProtein.add(item.getProtein() != null ? item.getProtein() : BigDecimal.ZERO);
                totalCarbs = totalCarbs.add(item.getCarbs() != null ? item.getCarbs() : BigDecimal.ZERO);
                totalFat = totalFat.add(item.getFat() != null ? item.getFat() : BigDecimal.ZERO);
            }
        }
        
        // 更新计划记录
        DailyRecipePlan plan = new DailyRecipePlan();
        plan.setId(planId);
        plan.setTotalCalories(totalCalories);
        plan.setTotalProtein(totalProtein);
        plan.setTotalCarbs(totalCarbs);
        plan.setTotalFat(totalFat);
        plan.setUpdatedAt(LocalDateTime.now());
        
        dailyRecipePlanMapper.updateById(plan);
    }
    
    @Override
    public void deleteRecipeFromTodayPlan(Long userId, Long itemId) {
        // 1. 查询食谱项
        DailyRecipePlanMealItem item = dailyRecipePlanMealItemMapper.selectById(itemId);
        if (item == null) {
            throw new RuntimeException("食谱项不存在");
        }
        
        // 2. 查询餐次记录
        DailyRecipePlanMeal meal = dailyRecipePlanMealMapper.selectById(item.getMealPlanId());
        if (meal == null) {
            throw new RuntimeException("餐次记录不存在");
        }
        
        // 3. 查询计划记录，验证用户权限
        DailyRecipePlan plan = dailyRecipePlanMapper.selectById(meal.getPlanId());
        if (plan == null || !plan.getUserId().equals(userId)) {
            throw new RuntimeException("没有权限删除");
        }
        
        // 4. 删除食谱项
        dailyRecipePlanMealItemMapper.deleteById(itemId);
        
        // 5. 更新计划的总营养值
        updatePlanNutrition(plan.getId());
        
        log.info("从今日食谱删除食谱项成功，用户：{}，itemId：{}", userId, itemId);
    }
}
