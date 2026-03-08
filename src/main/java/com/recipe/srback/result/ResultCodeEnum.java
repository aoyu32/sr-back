package com.recipe.srback.result;

import lombok.Getter;

/**
 * 状态码枚举
 */
@Getter
public enum ResultCodeEnum {
    
    // 成功
    SUCCESS(200, "操作成功"),
    
    // 客户端错误 4xx
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权，请先登录"),
    FORBIDDEN(403, "没有权限访问"),
    NOT_FOUND(404, "请求的资源不存在"),
    
    // 业务错误 10xx
    EMAIL_ALREADY_EXISTS(1001, "该邮箱已被注册"),
    EMAIL_NOT_EXISTS(1002, "该邮箱未注册"),
    EMAIL_OR_PASSWORD_ERROR(1003, "邮箱或密码错误"),
    ACCOUNT_DISABLED(1004, "账号已被禁用"),
    PASSWORD_NOT_MATCH(1005, "两次密码不一致"),
    VERIFICATION_CODE_ERROR(1006, "验证码错误或已过期"),
    VERIFICATION_CODE_SEND_FREQUENTLY(1007, "验证码发送过于频繁，请稍后再试"),
    TOKEN_INVALID(1008, "Token无效或已过期"),
    
    // 服务器错误 5xx
    INTERNAL_SERVER_ERROR(500, "系统内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂时不可用");
    
    private final Integer code;
    private final String message;
    
    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
