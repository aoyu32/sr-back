package com.recipe.srback.controller;

import com.recipe.srback.dto.XiaozhiChatRequestDTO;
import com.recipe.srback.result.Result;
import com.recipe.srback.service.XiaozhiChatService;
import com.recipe.srback.vo.XiaozhiMessageVO;
import com.recipe.srback.vo.XiaozhiSessionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * 小智AI咨询控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/xiaozhi")
@RequiredArgsConstructor
@Tag(name = "小智咨询", description = "小智AI咨询相关接口")
public class XiaozhiChatController {

    private final XiaozhiChatService xiaozhiChatService;

    @GetMapping("/sessions")
    @Operation(summary = "查询会话列表")
    public Result<List<XiaozhiSessionVO>> listSessions(@RequestAttribute("userId") Long userId) {
        return Result.success(xiaozhiChatService.listSessions(userId));
    }

    @GetMapping("/sessions/{sessionId}/messages")
    @Operation(summary = "查询会话消息列表")
    public Result<List<XiaozhiMessageVO>> listMessages(@RequestAttribute("userId") Long userId,
                                                       @PathVariable Long sessionId) {
        return Result.success(xiaozhiChatService.listMessages(userId, sessionId));
    }

    @DeleteMapping("/sessions/{sessionId}")
    @Operation(summary = "删除会话")
    public Result<Void> deleteSession(@RequestAttribute("userId") Long userId,
                                      @PathVariable Long sessionId) {
        xiaozhiChatService.deleteSession(userId, sessionId);
        return Result.success();
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "小智流式对话")
    public SseEmitter streamChat(@RequestAttribute("userId") Long userId,
                                 @RequestBody XiaozhiChatRequestDTO dto) {
        log.info("小智流式对话，请求用户：{}，会话：{}", userId, dto.getSessionId());
        return xiaozhiChatService.streamChat(userId, dto);
    }
}
