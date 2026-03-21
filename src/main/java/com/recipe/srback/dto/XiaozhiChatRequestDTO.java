package com.recipe.srback.dto;

import lombok.Data;

/**
 * 小智对话请求DTO
 */
@Data
public class XiaozhiChatRequestDTO {

    /**
     * 会话ID，首次对话可不传
     */
    private Long sessionId;

    /**
     * 文本内容
     */
    private String content;

    /**
     * 图片URL
     */
    private String imageUrl;
}
