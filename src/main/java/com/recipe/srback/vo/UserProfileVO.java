package com.recipe.srback.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 用户信息VO
 */
@Data
public class UserProfileVO {
    
    private Long id;
    
    private String email;
    
    private String avatar;
    
    private String nickname;
    
    private BigDecimal height;
    
    private BigDecimal weight;
    
    private BigDecimal bmi;
    
    private String bmiStatus;
    
    private LocalDate birthday;
    
    private Integer gender;
}
