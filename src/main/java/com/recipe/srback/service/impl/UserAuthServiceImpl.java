package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.recipe.srback.dto.LoginDTO;
import com.recipe.srback.dto.RegisterDTO;
import com.recipe.srback.dto.ResetPasswordDTO;
import com.recipe.srback.entity.User;
import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.mapper.UserMapper;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.service.EmailService;
import com.recipe.srback.service.UserAuthService;
import com.recipe.srback.utils.JwtUtil;
import com.recipe.srback.utils.PasswordUtil;
import com.recipe.srback.utils.VerificationCodeUtil;
import com.recipe.srback.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

/**
 * 用户认证服务实现
 */
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {
    
    private final UserMapper userMapper;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    private final VerificationCodeUtil verificationCodeUtil;
    private final EmailService emailService;
    
    @Value("${user.default.nickname-prefix}")
    private String nicknamePrefix;
    
    @Value("${user.default.avatar}")
    private String defaultAvatar;
    
    /**
     * 用户登录
     */
    @Override
    public UserVO login(LoginDTO loginDTO) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, loginDTO.getEmail());
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.EMAIL_OR_PASSWORD_ERROR);
        }
        
        // 验证密码
        if (!passwordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCodeEnum.EMAIL_OR_PASSWORD_ERROR);
        }
        
        // 检查账号状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCodeEnum.ACCOUNT_DISABLED);
        }
        
        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        
        // 构建返回对象
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setToken(token);
        
        return userVO;
    }
    
    /**
     * 用户注册
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO registerDTO) {
        // 验证两次密码是否一致
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException(ResultCodeEnum.PASSWORD_NOT_MATCH);
        }
        
        // 验证验证码
        if (!verificationCodeUtil.verifyCode(registerDTO.getEmail(), registerDTO.getCode(), "register")) {
            throw new BusinessException(ResultCodeEnum.VERIFICATION_CODE_ERROR);
        }
        
        // 检查邮箱是否已注册
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, registerDTO.getEmail());
        Long count = userMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResultCodeEnum.EMAIL_ALREADY_EXISTS);
        }
        
        // 创建用户
        User user = new User();
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordUtil.encode(registerDTO.getPassword()));
        user.setGender(0);
        user.setStatus(1);
        user.setAvatar(defaultAvatar);
        
        // 插入用户，获取生成的ID
        userMapper.insert(user);
        
        // 设置默认昵称：前缀-ID后6位
        String idStr = user.getId().toString();
        String shortId = idStr.length() > 6 ? idStr.substring(idStr.length() - 6) : idStr;
        user.setNickname(nicknamePrefix + "-" + shortId);
        userMapper.updateById(user);
    }
    
    /**
     * 重置密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        // 验证两次密码是否一致
        if (!resetPasswordDTO.getPassword().equals(resetPasswordDTO.getConfirmPassword())) {
            throw new BusinessException(ResultCodeEnum.PASSWORD_NOT_MATCH);
        }
        
        // 验证验证码
        if (!verificationCodeUtil.verifyCode(resetPasswordDTO.getEmail(), resetPasswordDTO.getCode(), "reset_password")) {
            throw new BusinessException(ResultCodeEnum.VERIFICATION_CODE_ERROR);
        }
        
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, resetPasswordDTO.getEmail());
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.EMAIL_NOT_EXISTS);
        }
        
        // 更新密码
        user.setPassword(passwordUtil.encode(resetPasswordDTO.getPassword()));
        userMapper.updateById(user);
    }
    
    /**
     * 发送验证码
     */
    @Override
    public void sendVerificationCode(String email, String type) {
        // 检查邮箱格式
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new BusinessException(ResultCodeEnum.BAD_REQUEST.getCode(), "邮箱格式不正确");
        }
        
        // 如果是注册，检查邮箱是否已存在
        if ("register".equals(type)) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, email);
            Long count = userMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BusinessException(ResultCodeEnum.EMAIL_ALREADY_EXISTS);
            }
        }
        
        // 如果是重置密码，检查邮箱是否存在
        if ("reset_password".equals(type)) {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, email);
            Long count = userMapper.selectCount(wrapper);
            if (count == 0) {
                throw new BusinessException(ResultCodeEnum.EMAIL_NOT_EXISTS);
            }
        }
        
        // 检查是否频繁发送
        if (verificationCodeUtil.hasValidCode(email)) {
            throw new BusinessException(ResultCodeEnum.VERIFICATION_CODE_SEND_FREQUENTLY);
        }
        
        // 生成6位数字验证码
        String code = String.format("%06d", new Random().nextInt(999999));
        
        // 保存验证码到内存
        verificationCodeUtil.saveCode(email, code, type);
        
        // 发送邮件
        emailService.sendVerificationCode(email, code, type);
    }
}
