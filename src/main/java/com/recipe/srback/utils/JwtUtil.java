package com.recipe.srback.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private String expiration;
    
    /**
     * 获取密钥
     */
    private SecretKey getSecretKey() {
        // 确保密钥长度足够（至少32字节）
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * 获取过期时间（毫秒）
     */
    private long getExpirationTime() {
        // 解析配置的过期时间，支持格式：7d（天）、24h（小时）、60m（分钟）
        String value = expiration.toLowerCase();
        long time = Long.parseLong(value.replaceAll("[^0-9]", ""));
        
        if (value.endsWith("d")) {
            return time * 24 * 60 * 60 * 1000; // 天
        } else if (value.endsWith("h")) {
            return time * 60 * 60 * 1000; // 小时
        } else if (value.endsWith("m")) {
            return time * 60 * 1000; // 分钟
        } else {
            return time; // 默认毫秒
        }
    }
    
    /**
     * 生成Token
     */
    public String generateToken(Long userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        
        long expirationTime = getExpirationTime();
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);
        
        log.debug("生成Token，用户ID：{}，过期时间：{}", userId, expirationDate);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 解析Token
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            boolean isValid = !expiration.before(new Date());
            
            if (!isValid) {
                log.warn("Token已过期，过期时间：{}", expiration);
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("Token验证失败：{}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 从Token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }
    
    /**
     * 从Token中获取邮箱
     */
    public String getEmailFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("email", String.class);
    }
}
