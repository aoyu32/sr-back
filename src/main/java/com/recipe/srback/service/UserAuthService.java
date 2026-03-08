package com.recipe.srback.service;

import com.recipe.srback.dto.LoginDTO;
import com.recipe.srback.dto.RegisterDTO;
import com.recipe.srback.dto.ResetPasswordDTO;
import com.recipe.srback.vo.UserVO;

/**
 * 用户认证服务接口
 */
public interface UserAuthService {
    
    /**
     * 用户登录
     */
    UserVO login(LoginDTO loginDTO);
    
    /**
     * 用户注册
     */
    void register(RegisterDTO registerDTO);
    
    /**
     * 重置密码
     */
    void resetPassword(ResetPasswordDTO resetPasswordDTO);
    
    /**
     * 发送验证码
     */
    void sendVerificationCode(String email, String type);
}
