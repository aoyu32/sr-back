package com.recipe.srback.utils;

/**
 * 邮件模板工具类
 */
public class EmailUtil {
    
    /**
     * 构建注册验证码邮件内容
     */
    public static String buildRegisterEmailContent(String code) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background: #B6CF99; color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                ".content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }" +
                ".code-box { background: white; border: 2px dashed #B6CF99; border-radius: 8px; padding: 20px; text-align: center; margin: 20px 0; }" +
                ".code { font-size: 32px; font-weight: bold; color: #B6CF99; letter-spacing: 5px; }" +
                ".tips { color: #666; font-size: 14px; margin-top: 20px; }" +
                ".footer { text-align: center; color: #999; font-size: 12px; margin-top: 20px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>欢迎注册食谱小智</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<p>您好！</p>" +
                "<p>感谢您注册食谱小智，您的验证码是：</p>" +
                "<div class='code-box'>" +
                "<div class='code'>" + code + "</div>" +
                "</div>" +
                "<div class='tips'>" +
                "<p>• 验证码有效期为5分钟，请尽快完成注册</p>" +
                "<p>• 如果这不是您本人的操作，请忽略此邮件</p>" +
                "</div>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>此邮件由系统自动发送，请勿回复</p>" +
                "<p>© 2026 食谱小智</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
    
    /**
     * 构建重置密码验证码邮件内容
     */
    public static String buildResetPasswordEmailContent(String code) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background: #B6CF99; color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                ".content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }" +
                ".code-box { background: white; border: 2px dashed #B6CF99; border-radius: 8px; padding: 20px; text-align: center; margin: 20px 0; }" +
                ".code { font-size: 32px; font-weight: bold; color: #B6CF99; letter-spacing: 5px; }" +
                ".tips { color: #666; font-size: 14px; margin-top: 20px; }" +
                ".warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 10px; margin: 15px 0; }" +
                ".footer { text-align: center; color: #999; font-size: 12px; margin-top: 20px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>重置密码</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<p>您好！</p>" +
                "<p>您正在进行密码重置操作，您的验证码是：</p>" +
                "<div class='code-box'>" +
                "<div class='code'>" + code + "</div>" +
                "</div>" +
                "<div class='warning'>" +
                "<strong>安全提示：</strong>请妥善保管您的验证码，不要泄露给他人" +
                "</div>" +
                "<div class='tips'>" +
                "<p>• 验证码有效期为5分钟</p>" +
                "<p>• 如果这不是您本人的操作，请立即修改密码并联系客服</p>" +
                "</div>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>此邮件由系统自动发送，请勿回复</p>" +
                "<p>© 2026 食谱小智</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
    
    /**
     * 构建默认验证码邮件内容
     */
    public static String buildDefaultEmailContent(String code) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background: #B6CF99; color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                ".content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }" +
                ".code-box { background: white; border: 2px dashed #B6CF99; border-radius: 8px; padding: 20px; text-align: center; margin: 20px 0; }" +
                ".code { font-size: 32px; font-weight: bold; color: #B6CF99; letter-spacing: 5px; }" +
                ".tips { color: #666; font-size: 14px; margin-top: 20px; }" +
                ".footer { text-align: center; color: #999; font-size: 12px; margin-top: 20px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>验证码</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<p>您好！</p>" +
                "<p>您的验证码是：</p>" +
                "<div class='code-box'>" +
                "<div class='code'>" + code + "</div>" +
                "</div>" +
                "<div class='tips'>" +
                "<p>• 验证码有效期为5分钟</p>" +
                "<p>• 如果这不是您本人的操作，请忽略此邮件</p>" +
                "</div>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>此邮件由系统自动发送，请勿回复</p>" +
                "<p>© 2026 食谱小智</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
