package com.recipe.srback.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 用户信息VO
 */
@Data
@Schema(description = "用户信息")
public class UserVO {
    
    @Schema(description = "用户ID")
    private Long id;
    
    @Schema(description = "邮箱")
    private String email;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "头像URL")
    private String avatar;
    
    @Schema(description = "性别：0-未知，1-男，2-女")
    private Integer gender;
    
    @Schema(description = "生日")
    private LocalDate birthday;
    
    @Schema(description = "Token")
    private String token;
}
