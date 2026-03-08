package com.recipe.srback.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 重置密码请求DTO
 */
@Data
@Schema(description = "重置密码请求")
public class ResetPasswordDTO {
    
    @Schema(description = "邮箱", example = "user@qq.com")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Schema(description = "验证码", example = "123456")
    @NotBlank(message = "验证码不能为空")
    private String code;
    
    @Schema(description = "新密码", example = "123456")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20位")
    private String password;
    
    @Schema(description = "确认新密码", example = "123456")
    @NotBlank(message = "确认新密码不能为空")
    private String confirmPassword;
}
