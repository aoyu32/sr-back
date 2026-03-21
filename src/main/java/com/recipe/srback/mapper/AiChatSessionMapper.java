package com.recipe.srback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recipe.srback.entity.AiChatSession;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI会话Mapper
 */
@Mapper
public interface AiChatSessionMapper extends BaseMapper<AiChatSession> {
}
