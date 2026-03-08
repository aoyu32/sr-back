package com.recipe.srback.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 */
public interface FileUploadService {
    
    /**
     * 上传头像
     */
    String uploadAvatar(Long userId, MultipartFile file);
    
    /**
     * 上传图片
     */
    String uploadImage(Long userId, MultipartFile file);
}
