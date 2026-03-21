package com.recipe.srback.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI消息实体
 */
@Data
@TableName("ai_chat_message")
public class AiChatMessage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long sessionId;

    private Long userId;

    private String role;

    private String messageType;

    private Integer sequenceNo;

    private String textContent;

    private String imageUrl;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
