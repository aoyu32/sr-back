package com.recipe.srback.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 小智消息VO
 */
@Data
public class XiaozhiMessageVO {

    private Long id;

    private String role;

    private String messageType;

    private String content;

    private String imageUrl;

    private LocalDateTime createdAt;
}
