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
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 获取过期时间
     */
    private long getExpirationTime() {
        String value = expiration.toLowerCase();
        long time = Long.parseLong(value.replaceAll("[^0-9]", ""));

        if (value.endsWith("d")) {
            return time * 24 * 60 * 60 * 1000;
        } else if (value.endsWith("h")) {
            return time * 60 * 60 * 1000;
        } else if (value.endsWith("m")) {
            return time * 60 * 1000;
        } else {
            return time;
        }
    }

    /**
     * 生成普通用户Token
     */
    public String generateToken(Long userId, String email) {
        return generateToken(userId, email, "user");
    }

    /**
     * 生成带角色的Token
     */
    public String generateToken(Long userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("role", role);

        long expirationTime = getExpirationTime();
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

        log.debug("生成Token，userId: {}, role: {}, expireAt: {}", userId, role, expirationDate);

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
     * 校验Token
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("Token校验失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    /**
     * 获取邮箱
     */
    public String getEmailFromToken(String token) {
        return parseToken(token).get("email", String.class);
    }

    /**
     * 获取角色
     */
    public String getRoleFromToken(String token) {
        String role = parseToken(token).get("role", String.class);
        return role == null || role.isEmpty() ? "user" : role;
    }
}
