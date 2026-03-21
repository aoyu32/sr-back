package com.recipe.srback.service;

import com.recipe.srback.dto.UserCreateAdminDTO;
import com.recipe.srback.dto.UserUpdateAdminDTO;
import com.recipe.srback.vo.PageAdminVO;
import com.recipe.srback.vo.UserAdminVO;

/**
 * 后台用户管理服务
 */
public interface UserAdminService {

    PageAdminVO<UserAdminVO> pageUsers(Integer pageNum, Integer pageSize, String keyword, Integer status);

    UserAdminVO getUserById(Long userId);

    Long createUser(UserCreateAdminDTO dto);

    void updateUser(Long userId, UserUpdateAdminDTO dto);

    void deleteUser(Long userId);
}
