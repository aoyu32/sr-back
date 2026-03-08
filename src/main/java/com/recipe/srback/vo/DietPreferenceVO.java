package com.recipe.srback.vo;

import lombok.Data;

/**
 * 饮食偏好VO
 */
@Data
public class DietPreferenceVO {
    private Long id;
    
    private String preferenceType;
    
    private String foodName;
}
