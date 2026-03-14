package com.recipe.srback.dto;

import lombok.Data;

/**
 * 修改邮箱DTO
 */
@Data
public class UpdateEmailDTO {
    private String newEmail;
    
    private String verificationCode;
}
