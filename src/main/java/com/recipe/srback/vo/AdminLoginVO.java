package com.recipe.srback.vo;

import lombok.Data;

/**
 * 管理员登录及资料信息
 */
@Data
public class AdminLoginVO {

    /**
     * 管理员ID
     */
    private Long id;

    /**
     * 管理员邮箱
     */
    private String email;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 管理员昵称
     */
    private String name;

    /**
     * 管理员头像
     */
    private String avatar;

    /**
     * 登录令牌
     */
    private String token;
}
