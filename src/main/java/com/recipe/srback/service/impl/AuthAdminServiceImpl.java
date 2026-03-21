package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recipe.srback.dto.LoginAdminDTO;
import com.recipe.srback.entity.User;
import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.mapper.UserMapper;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.service.AuthAdminService;
import com.recipe.srback.utils.JwtUtil;
import com.recipe.srback.utils.PasswordUtil;
import com.recipe.srback.vo.AdminLoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 后台管理员认证服务实现类
 */
@Service
@RequiredArgsConstructor
public class AuthAdminServiceImpl implements AuthAdminService {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final PasswordUtil passwordUtil;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.name}")
    private String adminName;

    @Value("${admin.avatar}")
    private String adminAvatar;

    /**
     * 管理员登录校验。
     * 目前后台固定使用 user 表中邮箱为配置项 admin.email 的账号作为唯一管理员账号。
     *
     * @param loginAdminDTO 登录参数
     * @return 登录结果
     */
    @Override
    public AdminLoginVO login(LoginAdminDTO loginAdminDTO) {
        if (!adminEmail.equals(loginAdminDTO.getUsername())) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED.getCode(), "管理员账号或密码错误");
        }

        User adminUser = getAdminUserByEmail();
        if (!passwordUtil.matches(loginAdminDTO.getPassword(), adminUser.getPassword())) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED.getCode(), "管理员账号或密码错误");
        }
        if (adminUser.getStatus() == null || adminUser.getStatus() == 0) {
            throw new BusinessException(ResultCodeEnum.ACCOUNT_DISABLED);
        }

        AdminLoginVO adminLoginVO = buildAdminVO(adminUser);
        adminLoginVO.setToken(jwtUtil.generateToken(adminUser.getId(), adminUser.getEmail(), "admin"));
        return adminLoginVO;
    }

    /**
     * 获取当前登录管理员资料。
     *
     * @param userId 当前管理员ID
     * @return 管理员资料
     */
    @Override
    public AdminLoginVO getProfile(Long userId) {
        User adminUser = userMapper.selectById(userId);
        if (adminUser == null || !adminEmail.equals(adminUser.getEmail())) {
            throw new BusinessException(ResultCodeEnum.FORBIDDEN.getCode(), "当前账号不是管理员");
        }
        return buildAdminVO(adminUser);
    }

    private User getAdminUserByEmail() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, adminEmail);
        User adminUser = userMapper.selectOne(wrapper);
        if (adminUser == null) {
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED.getCode(), "管理员账号不存在，请先执行管理员初始化SQL");
        }
        return adminUser;
    }

    private AdminLoginVO buildAdminVO(User adminUser) {
        AdminLoginVO adminLoginVO = new AdminLoginVO();
        adminLoginVO.setId(adminUser.getId());
        adminLoginVO.setEmail(adminUser.getEmail());
        adminLoginVO.setUsername(adminUser.getEmail());
        adminLoginVO.setName(StringUtils.hasText(adminUser.getNickname()) ? adminUser.getNickname() : adminName);
        adminLoginVO.setAvatar(StringUtils.hasText(adminUser.getAvatar()) ? adminUser.getAvatar() : adminAvatar);
        return adminLoginVO;
    }
}
