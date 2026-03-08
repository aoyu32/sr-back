package com.recipe.srback.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 阿里云OSS工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "aliyun.oss", name = "enabled", havingValue = "true")
public class AliOssUtil {
    
    private final OSS ossClient;
    
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    
    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;
    
    /**
     * 上传文件
     * 
     * @param file 文件
     * @param folder 文件夹路径（如：avatar/、recipe/）
     * @return 文件访问URL
     */
    public String upload(MultipartFile file, String folder) {
        try {
            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new RuntimeException("文件名不能为空");
            }
            
            // 生成唯一文件名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = folder + UUID.randomUUID().toString() + extension;
            
            // 上传文件
            InputStream inputStream = file.getInputStream();
            ossClient.putObject(bucketName, fileName, inputStream);
            
            // 返回文件访问URL
            String url = "https://" + bucketName + "." + endpoint + "/" + fileName;
            log.info("文件上传成功，URL：{}", url);
            return url;
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 上传字节数组
     * 
     * @param bytes 字节数组
     * @param folder 文件夹路径
     * @param extension 文件扩展名（如：.jpg）
     * @return 文件访问URL
     */
    public String upload(byte[] bytes, String folder, String extension) {
        try {
            // 生成唯一文件名
            String fileName = folder + UUID.randomUUID().toString() + extension;
            
            // 上传文件
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            ossClient.putObject(bucketName, fileName, inputStream);
            
            // 返回文件访问URL
            String url = "https://" + bucketName + "." + endpoint + "/" + fileName;
            log.info("文件上传成功，URL：{}", url);
            return url;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 上传输入流
     * 
     * @param inputStream 输入流
     * @param folder 文件夹路径
     * @param fileName 文件名
     * @return 文件访问URL
     */
    public String upload(InputStream inputStream, String folder, String fileName) {
        try {
            // 生成完整文件路径
            String fullPath = folder + fileName;
            
            // 上传文件
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fullPath, inputStream);
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            
            // 返回文件访问URL
            String url = "https://" + bucketName + "." + endpoint + "/" + fullPath;
            log.info("文件上传成功，URL：{}", url);
            return url;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除文件
     * 
     * @param fileUrl 文件URL
     */
    public void delete(String fileUrl) {
        try {
            // 从URL中提取文件路径
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            ossClient.deleteObject(bucketName, fileName);
            log.info("文件删除成功，URL：{}", fileUrl);
        } catch (Exception e) {
            log.error("文件删除失败", e);
            throw new RuntimeException("文件删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除文件（通过文件路径）
     * 
     * @param filePath 文件在OSS中的路径
     */
    public void deleteByPath(String filePath) {
        try {
            ossClient.deleteObject(bucketName, filePath);
            log.info("文件删除成功，路径：{}", filePath);
        } catch (Exception e) {
            log.error("文件删除失败", e);
            throw new RuntimeException("文件删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 判断文件是否存在
     * 
     * @param filePath 文件在OSS中的路径
     * @return 是否存在
     */
    public boolean exists(String filePath) {
        try {
            return ossClient.doesObjectExist(bucketName, filePath);
        } catch (Exception e) {
            log.error("检查文件是否存在失败", e);
            return false;
        }
    }
    
    /**
     * 获取文件访问URL
     * 
     * @param filePath 文件在OSS中的路径
     * @return 文件访问URL
     */
    public String getUrl(String filePath) {
        return "https://" + bucketName + "." + endpoint + "/" + filePath;
    }
}
