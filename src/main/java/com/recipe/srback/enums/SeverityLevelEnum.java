package com.recipe.srback.enums;

import lombok.Getter;

/**
 * 严重程度枚举
 */
@Getter
public enum SeverityLevelEnum {
    LOW("low", "轻微"),
    MEDIUM("medium", "中等"),
    HIGH("high", "严重");
    
    private final String code;
    private final String name;
    
    SeverityLevelEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public static String getNameByCode(String code) {
        for (SeverityLevelEnum level : values()) {
            if (level.getCode().equals(code)) {
                return level.getName();
            }
        }
        return code;
    }
}
