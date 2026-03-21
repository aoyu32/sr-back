package com.recipe.srback.service;

import com.recipe.srback.dto.LoginAdminDTO;
import com.recipe.srback.vo.AdminLoginVO;

/**
 * 后台管理员认证服务
 */
public interface AuthAdminService {

    /**
     * 管理员登录
     *
     * @param loginAdminDTO 登录参数
     * @return 登录结果
     */
    AdminLoginVO login(LoginAdminDTO loginAdminDTO);

    /**
     * 获取当前管理员资料
     *
     * @param userId 当前管理员用户ID
     * @return 管理员资料
     */
    AdminLoginVO getProfile(Long userId);
}
