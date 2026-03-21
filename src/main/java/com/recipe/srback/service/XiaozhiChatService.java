package com.recipe.srback.service;

import com.recipe.srback.dto.XiaozhiChatRequestDTO;
import com.recipe.srback.vo.XiaozhiMessageVO;
import com.recipe.srback.vo.XiaozhiSessionVO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * 小智对话服务接口
 */
public interface XiaozhiChatService {

    /**
     * 查询会话列表
     */
    List<XiaozhiSessionVO> listSessions(Long userId);

    /**
     * 查询会话消息
     */
    List<XiaozhiMessageVO> listMessages(Long userId, Long sessionId);

    /**
     * 删除会话
     */
    void deleteSession(Long userId, Long sessionId);

    /**
     * 流式对话
     */
    SseEmitter streamChat(Long userId, XiaozhiChatRequestDTO dto);
}
