package com.recipe.srback.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 小智会话VO
 */
@Data
public class XiaozhiSessionVO {

    private Long id;

    private String title;

    private String preview;

    private Integer messageCount;

    private LocalDateTime lastMessageAt;
}
