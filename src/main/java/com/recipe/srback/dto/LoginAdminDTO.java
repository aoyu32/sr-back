package com.recipe.srback.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 管理员登录参数
 */
@Data
public class LoginAdminDTO {

    @NotBlank(message = "请输入管理员账号")
    private String username;

    @NotBlank(message = "请输入管理员密码")
    private String password;
}
