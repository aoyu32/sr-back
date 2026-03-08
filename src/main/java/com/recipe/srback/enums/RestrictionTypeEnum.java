package com.recipe.srback.enums;

import lombok.Getter;

/**
 * 特殊禁忌类型枚举
 */
@Getter
public enum RestrictionTypeEnum {
    ALLERGY("allergy", "过敏"),
    DISEASE("disease", "疾病");
    
    private final String code;
    private final String name;
    
    RestrictionTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    public static String getNameByCode(String code) {
        for (RestrictionTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type.getName();
            }
        }
        return code;
    }
}
