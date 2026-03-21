package com.recipe.srback.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 后台新增用户参数
 */
@Data
public class UserCreateAdminDTO {

    @NotBlank(message = "请输入邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "请输入密码")
    private String password;

    private String nickname;

    private String avatar;

    @NotNull(message = "请选择性别")
    private Integer gender;

    private LocalDate birthday;

    @NotNull(message = "请选择账号状态")
    private Integer status;
}
