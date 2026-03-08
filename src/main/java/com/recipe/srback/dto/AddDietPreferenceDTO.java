package com.recipe.srback.dto;

import lombok.Data;

/**
 * 添加饮食偏好DTO
 */
@Data
public class AddDietPreferenceDTO {
    private String preferenceType;
    
    private String foodName;
}
