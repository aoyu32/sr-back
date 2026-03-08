package com.recipe.srback.service.impl;

import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.service.FileUploadService;
import com.recipe.srback.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传服务实现
 */
@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {
    
    @Autowired(required = false)
    private AliOssUtil aliOssUtil;
    
    @Value("${aliyun.oss.enabled:false}")
    private Boolean ossEnabled;
    
    // 允许的图片格式
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    // 最大文件大小：5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    
    /**
     * 上传头像
     */
    @Override
    public String uploadAvatar(Long userId, MultipartFile file) {
        log.info("开始上传头像，用户ID：{}", userId);
        
        // 验证文件
        validateImageFile(file);
        
        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String fileName = "avatar/" + userId + "/" + UUID.randomUUID().toString() + extension;
        
        log.info("生成的文件名：{}", fileName);
        
        // 上传文件
        String url = uploadFile(file, fileName);
        
        log.info("头像上传完成，返回URL：{}", url);
        
        return url;
    }
    
    /**
     * 上传图片
     */
    @Override
    public String uploadImage(Long userId, MultipartFile file) {
        // 验证文件
        validateImageFile(file);
        
        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String fileName = "image/" + userId + "/" + UUID.randomUUID().toString() + extension;
        
        // 上传文件
        return uploadFile(file, fileName);
    }
    
    /**
     * 验证图片文件
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCodeEnum.BAD_REQUEST.getCode(), "文件不能为空");
        }
        
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException(ResultCodeEnum.BAD_REQUEST.getCode(), 
                    "不支持的文件格式，仅支持 JPG、PNG、GIF、WEBP");
        }
        
        // 验证文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ResultCodeEnum.BAD_REQUEST.getCode(), 
                    "文件大小不能超过5MB");
        }
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return ".jpg";
        }
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return ".jpg";
        }
        return filename.substring(lastDotIndex);
    }
    
    /**
     * 上传文件
     */
    private String uploadFile(MultipartFile file, String fileName) {
        try {
            if (ossEnabled && aliOssUtil != null) {
                // 使用阿里云OSS上传
                // 从fileName中提取文件夹路径和文件名
                int lastSlashIndex = fileName.lastIndexOf("/");
                String folder = fileName.substring(0, lastSlashIndex + 1);
                String fileNameOnly = fileName.substring(lastSlashIndex + 1);
                
                return aliOssUtil.upload(file.getInputStream(), folder, fileNameOnly);
            } else {
                // OSS未启用，返回默认头像
                log.warn("OSS未启用，无法上传文件：{}", fileName);
                return "https://smart-recipe.oss-cn-beijing.aliyuncs.com/default-avatar.png";
            }
        } catch (IOException e) {
            log.error("文件上传失败：{}", fileName, e);
            throw new BusinessException(ResultCodeEnum.INTERNAL_SERVER_ERROR.getCode(), 
                    "文件上传失败，请稍后重试");
        }
    }
}
