package com.recipe.srback.controller;

import com.recipe.srback.result.Result;
import com.recipe.srback.service.FileAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 后台文件管理控制器
 */
@RestController
@RequestMapping("/api/admin/file")
@RequiredArgsConstructor
public class FileAdminController {

    private final FileAdminService fileAdminService;

    /**
     * 上传图片
     */
    @PostMapping("/upload/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return Result.success(fileAdminService.uploadImage(file));
    }
}
