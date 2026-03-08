package com.recipe.srback.service.impl;

import com.recipe.srback.service.EmailService;
import com.recipe.srback.utils.EmailUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * 邮件服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String from;
    
    /**
     * 发送验证码邮件
     */
    @Override
    public void sendVerificationCode(String to, String code, String type) {
        try {
            String subject;
            String content;
            
            if ("register".equals(type)) {
                subject = "【食谱小智】注册验证码";
                content = EmailUtil.buildRegisterEmailContent(code);
            } else if ("reset_password".equals(type)) {
                subject = "【食谱小智】重置密码验证码";
                content = EmailUtil.buildResetPasswordEmailContent(code);
            } else {
                subject = "【食谱小智】验证码";
                content = EmailUtil.buildDefaultEmailContent(code);
            }
            
            sendHtmlMail(to, subject, content);
            log.info("验证码邮件发送成功，收件人：{}, 类型：{}", to, type);
        } catch (Exception e) {
            log.error("验证码邮件发送失败，收件人：{}, 类型：{}", to, type, e);
            throw new RuntimeException("邮件发送失败，请稍后重试");
        }
    }
    
    /**
     * 发送普通文本邮件
     */
    @Override
    public void sendTextMail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            log.info("文本邮件发送成功，收件人：{}, 主题：{}", to, subject);
        } catch (Exception e) {
            log.error("文本邮件发送失败，收件人：{}, 主题：{}", to, subject, e);
            throw new RuntimeException("邮件发送失败，请稍后重试");
        }
    }
    
    /**
     * 发送HTML邮件
     */
    @Override
    public void sendHtmlMail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            
            mailSender.send(message);
            log.info("HTML邮件发送成功，收件人：{}, 主题：{}", to, subject);
        } catch (MessagingException e) {
            log.error("HTML邮件发送失败，收件人：{}, 主题：{}", to, subject, e);
            throw new RuntimeException("邮件发送失败，请稍后重试");
        }
    }
}
