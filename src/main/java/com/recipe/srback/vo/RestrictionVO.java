package com.recipe.srback.vo;

import lombok.Data;

/**
 * 特殊禁忌VO
 */
@Data
public class RestrictionVO {
    private Long id;
    
    private String type;
    
    private String typeName;
    
    private String name;
    
    private String description;
    
    private String severity;
    
    private String severityName;
    
    private String addedDate;
}
