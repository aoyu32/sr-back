package com.recipe.srback.interceptor;

import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.result.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理员权限拦截器
 */
@Component
public class PermissionAdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String role = (String) request.getAttribute("role");
        if (!"admin".equals(role)) {
            throw new BusinessException(ResultCodeEnum.FORBIDDEN.getCode(), "仅管理员可访问后台接口");
        }
        return true;
    }
}
