package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.recipe.srback.dto.UserCreateAdminDTO;
import com.recipe.srback.dto.UserUpdateAdminDTO;
import com.recipe.srback.entity.User;
import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.mapper.UserMapper;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.service.UserAdminService;
import com.recipe.srback.utils.PasswordUtil;
import com.recipe.srback.vo.PageAdminVO;
import com.recipe.srback.vo.UserAdminVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 后台用户管理服务实现
 */
@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {

    private final UserMapper userMapper;
    private final PasswordUtil passwordUtil;

    @Value("${user.default.avatar}")
    private String defaultAvatar;

    @Override
    public PageAdminVO<UserAdminVO> pageUsers(Integer pageNum, Integer pageSize, String keyword, Integer status) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(User::getCreatedAt);

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getEmail, keyword).or().like(User::getNickname, keyword));
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }

        Page<User> userPage = userMapper.selectPage(page, wrapper);
        List<UserAdminVO> records = userPage.getRecords().stream().map(this::toUserAdminVO).toList();
        return PageAdminVO.of(pageNum, pageSize, userPage.getTotal(), records);
    }

    @Override
    public UserAdminVO getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.USER_NOT_FOUND);
        }
        return toUserAdminVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(UserCreateAdminDTO dto) {
        checkEmailExists(dto.getEmail(), null);

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordUtil.encode(dto.getPassword()));
        user.setNickname(StringUtils.hasText(dto.getNickname()) ? dto.getNickname() : buildDefaultNickname(dto.getEmail()));
        user.setAvatar(StringUtils.hasText(dto.getAvatar()) ? dto.getAvatar() : defaultAvatar);
        user.setGender(dto.getGender());
        user.setBirthday(dto.getBirthday());
        user.setStatus(dto.getStatus());
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long userId, UserUpdateAdminDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.USER_NOT_FOUND);
        }

        checkEmailExists(dto.getEmail(), userId);
        user.setEmail(dto.getEmail());
        user.setNickname(StringUtils.hasText(dto.getNickname()) ? dto.getNickname() : buildDefaultNickname(dto.getEmail()));
        user.setAvatar(StringUtils.hasText(dto.getAvatar()) ? dto.getAvatar() : defaultAvatar);
        user.setGender(dto.getGender());
        user.setBirthday(dto.getBirthday());
        user.setStatus(dto.getStatus());
        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(passwordUtil.encode(dto.getPassword()));
        }
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.USER_NOT_FOUND);
        }
        userMapper.deleteById(userId);
    }

    private void checkEmailExists(String email, Long excludeUserId) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        if (excludeUserId != null) {
            wrapper.ne(User::getId, excludeUserId);
        }
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCodeEnum.EMAIL_ALREADY_EXISTS);
        }
    }

    private String buildDefaultNickname(String email) {
        int index = email.indexOf("@");
        return index > 0 ? email.substring(0, index) : email;
    }

    private UserAdminVO toUserAdminVO(User user) {
        UserAdminVO vo = new UserAdminVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
