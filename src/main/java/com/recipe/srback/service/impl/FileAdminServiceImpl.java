package com.recipe.srback.service.impl;

import com.recipe.srback.service.FileAdminService;
import com.recipe.srback.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 后台文件管理服务实现
 */
@Service
@RequiredArgsConstructor
public class FileAdminServiceImpl implements FileAdminService {

    private final FileUploadService fileUploadService;

    @Override
    public String uploadImage(MultipartFile file) {
        return fileUploadService.uploadImage(0L, file);
    }
}
