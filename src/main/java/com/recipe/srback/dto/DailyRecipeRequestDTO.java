package com.recipe.srback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 每日食谱推荐请求DTO
 */
@Data
public class DailyRecipeRequestDTO {
    
    private String date;
    
    private String input;
    
    @JsonProperty("health_goal")
    private HealthGoalDTO healthGoal;
    
    private List<RestrictionDTO> restriction;
    
    @JsonProperty("diet_preference")
    private List<DietPreferenceDTO> dietPreference;
    
    @Data
    public static class HealthGoalDTO {
        private Long id;
        
        @JsonProperty("goal_type")
        private String goalType;
        
        @JsonProperty("target_weight")
        private Double targetWeight;
        
        @JsonProperty("target_bmi")
        private Double targetBmi;
        
        @JsonProperty("target_muscle")
        private Double targetMuscle;
        
        @JsonProperty("target_blood_sugar")
        private Double targetBloodSugar;
        
        @JsonProperty("target_blood_pressure")
        private String targetBloodPressure;
        
        @JsonProperty("daily_calories")
        private Integer dailyCalories;
        
        @JsonProperty("daily_protein")
        private Double dailyProtein;
        
        @JsonProperty("daily_carbs")
        private Double dailyCarbs;
        
        @JsonProperty("daily_sodium")
        private Double dailySodium;
        
        @JsonProperty("start_date")
        private String startDate;
        
        @JsonProperty("end_date")
        private String endDate;
        
        private String status;
    }
    
    @Data
    public static class RestrictionDTO {
        private Long id;
        private String type;
        private String name;
        private String description;
        private String severity;
    }
    
    @Data
    public static class DietPreferenceDTO {
        private Long id;
        
        @JsonProperty("preference_type")
        private String preferenceType;
        
        @JsonProperty("food_name")
        private String foodName;
    }
}
