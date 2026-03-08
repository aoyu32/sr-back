package com.recipe.srback.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码工具类
 */
@Component
public class VerificationCodeUtil {
    
    /**
     * 验证码存储 Map<email, CodeInfo>
     */
    private final Map<String, CodeInfo> codeStore = new ConcurrentHashMap<>();
    
    /**
     * 保存验证码
     */
    public void saveCode(String email, String code, String type) {
        CodeInfo codeInfo = new CodeInfo();
        codeInfo.setCode(code);
        codeInfo.setType(type);
        codeInfo.setExpireTime(LocalDateTime.now().plusMinutes(5)); // 5分钟过期
        codeStore.put(email, codeInfo);
    }
    
    /**
     * 验证验证码
     */
    public boolean verifyCode(String email, String code, String type) {
        CodeInfo codeInfo = codeStore.get(email);
        if (codeInfo == null) {
            return false;
        }
        
        // 检查是否过期
        if (LocalDateTime.now().isAfter(codeInfo.getExpireTime())) {
            codeStore.remove(email);
            return false;
        }
        
        // 检查类型和验证码
        if (!type.equals(codeInfo.getType()) || !code.equals(codeInfo.getCode())) {
            return false;
        }
        
        // 验证成功后删除验证码
        codeStore.remove(email);
        return true;
    }
    
    /**
     * 检查验证码是否存在且未过期
     */
    public boolean hasValidCode(String email) {
        CodeInfo codeInfo = codeStore.get(email);
        if (codeInfo == null) {
            return false;
        }
        
        // 检查是否过期
        if (LocalDateTime.now().isAfter(codeInfo.getExpireTime())) {
            codeStore.remove(email);
            return false;
        }
        
        return true;
    }
    
    /**
     * 验证码信息
     */
    private static class CodeInfo {
        private String code;
        private String type;
        private LocalDateTime expireTime;
        
        public String getCode() {
            return code;
        }
        
        public void setCode(String code) {
            this.code = code;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public LocalDateTime getExpireTime() {
            return expireTime;
        }
        
        public void setExpireTime(LocalDateTime expireTime) {
            this.expireTime = expireTime;
        }
    }
}
