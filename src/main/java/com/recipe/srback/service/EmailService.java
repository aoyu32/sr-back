package com.recipe.srback.service;

/**
 * 邮件服务接口
 */
public interface EmailService {
    
    /**
     * 发送验证码邮件
     * 
     * @param to 收件人邮箱
     * @param code 验证码
     * @param type 类型：register-注册，reset_password-重置密码
     */
    void sendVerificationCode(String to, String code, String type);
    
    /**
     * 发送普通文本邮件
     * 
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    void sendTextMail(String to, String subject, String content);
    
    /**
     * 发送HTML邮件
     * 
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content HTML内容
     */
    void sendHtmlMail(String to, String subject, String content);
}
