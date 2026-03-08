package com.recipe.srback.controller;

import com.recipe.srback.result.Result;
import com.recipe.srback.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileUploadController {
    
    private final FileUploadService fileUploadService;
    
    /**
     * 上传头像
     */
    @PostMapping("/upload/avatar")
    public Result<String> uploadAvatar(
            @RequestAttribute("userId") Long userId,
            @RequestParam("file") MultipartFile file) {
        String avatarUrl = fileUploadService.uploadAvatar(userId, file);
        return Result.success(avatarUrl);
    }
    
    /**
     * 上传图片
     */
    @PostMapping("/upload/image")
    public Result<String> uploadImage(
            @RequestAttribute("userId") Long userId,
            @RequestParam("file") MultipartFile file) {
        String imageUrl = fileUploadService.uploadImage(userId, file);
        return Result.success(imageUrl);
    }
}
