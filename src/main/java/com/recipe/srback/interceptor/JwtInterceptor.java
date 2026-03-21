package com.recipe.srback.interceptor;

import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            token = request.getHeader("token");
        }
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token == null || token.isEmpty()) {
            log.warn("请求未携带Token: {}", request.getRequestURI());
            throw new BusinessException(ResultCodeEnum.UNAUTHORIZED);
        }

        if (!jwtUtil.validateToken(token)) {
            log.warn("Token无效或已过期: {}", token);
            throw new BusinessException(ResultCodeEnum.TOKEN_INVALID);
        }

        request.setAttribute("userId", jwtUtil.getUserIdFromToken(token));
        request.setAttribute("role", jwtUtil.getRoleFromToken(token));
        return true;
    }
}
