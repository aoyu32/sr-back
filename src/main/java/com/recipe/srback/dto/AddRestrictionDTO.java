package com.recipe.srback.dto;

import lombok.Data;

/**
 * 添加特殊禁忌DTO
 */
@Data
public class AddRestrictionDTO {
    private String type;
    
    private String name;
    
    private String description;
    
    private String severity;
}
