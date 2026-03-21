package com.recipe.srback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recipe.srback.entity.AiChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI消息Mapper
 */
@Mapper
public interface AiChatMessageMapper extends BaseMapper<AiChatMessage> {
}
