package com.recipe.srback.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 后台文件管理服务
 */
public interface FileAdminService {

    String uploadImage(MultipartFile file);
}
