package com.recipe.srback.enums;

import lombok.Getter;

/**
 * 健康目标类型枚举
 */
@Getter
public enum HealthGoalTypeEnum {
    
    LOSE_WEIGHT("lose_weight", "减重"),
    GAIN_WEIGHT("gain_weight", "增重"),
    MAINTAIN("maintain", "保持健康"),
    GAIN_MUSCLE("gain_muscle", "增肌"),
    CONTROL_SUGAR("control_sugar", "控糖"),
    LOWER_PRESSURE("lower_pressure", "降压");
    
    private final String code;
    private final String name;
    
    HealthGoalTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    /**
     * 根据code获取name
     */
    public static String getNameByCode(String code) {
        for (HealthGoalTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type.getName();
            }
        }
        return null;
    }
    
    /**
     * 根据code获取枚举
     */
    public static HealthGoalTypeEnum getByCode(String code) {
        for (HealthGoalTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 验证code是否有效
     */
    public static boolean isValidCode(String code) {
        return getByCode(code) != null;
    }
}
