package com.recipe.srback.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 后台用户信息
 */
@Data
public class UserAdminVO {

    private Long id;

    private String email;

    private String nickname;

    private String avatar;

    private Integer gender;

    private LocalDate birthday;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
