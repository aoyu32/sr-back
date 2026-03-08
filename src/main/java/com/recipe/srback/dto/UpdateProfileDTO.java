package com.recipe.srback.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 更新用户信息DTO
 */
@Data
public class UpdateProfileDTO {
    
    private String nickname;
    
    private String avatar;
    
    private Integer gender;
    
    private LocalDate birthday;
    
    private BigDecimal height;
    
    private BigDecimal weight;
}
