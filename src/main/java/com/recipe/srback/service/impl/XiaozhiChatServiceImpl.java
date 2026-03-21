package com.recipe.srback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe.srback.config.CozeConfig;
import com.recipe.srback.dto.XiaozhiChatRequestDTO;
import com.recipe.srback.entity.AiChatMessage;
import com.recipe.srback.entity.AiChatSession;
import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.mapper.AiChatMessageMapper;
import com.recipe.srback.mapper.AiChatSessionMapper;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.service.XiaozhiChatService;
import com.recipe.srback.vo.XiaozhiMessageVO;
import com.recipe.srback.vo.XiaozhiSessionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 小智对话服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class XiaozhiChatServiceImpl implements XiaozhiChatService {

    private static final String DEFAULT_IMAGE_PROMPT = "请帮我看看这张图片，并结合饮食健康场景给出回答。";

    private final AiChatSessionMapper aiChatSessionMapper;
    private final AiChatMessageMapper aiChatMessageMapper;
    private final CozeConfig cozeConfig;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public List<XiaozhiSessionVO> listSessions(Long userId) {
        LambdaQueryWrapper<AiChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatSession::getUserId, userId)
                .eq(AiChatSession::getStatus, 1)
                .orderByDesc(AiChatSession::getLastMessageAt)
                .orderByDesc(AiChatSession::getCreatedAt);

        return aiChatSessionMapper.selectList(wrapper).stream().map(this::toSessionVO).toList();
    }

    @Override
    public List<XiaozhiMessageVO> listMessages(Long userId, Long sessionId) {
        getSessionOrThrow(userId, sessionId);

        LambdaQueryWrapper<AiChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatMessage::getUserId, userId)
                .eq(AiChatMessage::getSessionId, sessionId)
                .eq(AiChatMessage::getStatus, 1)
                .orderByAsc(AiChatMessage::getSequenceNo);

        return aiChatMessageMapper.selectList(wrapper).stream().map(this::toMessageVO).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSession(Long userId, Long sessionId) {
        AiChatSession session = getSessionOrThrow(userId, sessionId);

        session.setStatus(0);
        session.setUpdatedAt(LocalDateTime.now());
        aiChatSessionMapper.updateById(session);

        LambdaQueryWrapper<AiChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatMessage::getUserId, userId)
                .eq(AiChatMessage::getSessionId, sessionId)
                .eq(AiChatMessage::getStatus, 1);

        List<AiChatMessage> messages = aiChatMessageMapper.selectList(wrapper);
        LocalDateTime now = LocalDateTime.now();
        for (AiChatMessage message : messages) {
            message.setStatus(0);
            message.setUpdatedAt(now);
            aiChatMessageMapper.updateById(message);
        }
    }

    @Override
    public SseEmitter streamChat(Long userId, XiaozhiChatRequestDTO dto) {
        if (!StringUtils.hasText(dto.getContent()) && !StringUtils.hasText(dto.getImageUrl())) {
            throw new BusinessException(ResultCodeEnum.BAD_REQUEST.getCode(), "消息内容和图片不能同时为空");
        }

        AiChatSession session = getOrCreateSession(userId, dto.getSessionId());
        UserMessage userMessage = buildSpringAiUserMessage(dto.getContent(), dto.getImageUrl());

        saveUserMessage(session, userId, userMessage);

        SseEmitter emitter = new SseEmitter(300000L);
        executor.execute(() -> doStreamChat(emitter, session, userId, userMessage));

        emitter.onTimeout(() -> {
            log.warn("小智对话SSE超时，sessionId={}", session.getId());
            emitter.complete();
        });
        emitter.onError(error -> log.error("小智对话SSE异常，sessionId={}", session.getId(), error));

        return emitter;
    }

    /**
     * 调用 Coze 聊天接口并将流式结果转发给前端。
     * 同时在会话结束后把最终的 assistant 回复落库。
     */
    private void doStreamChat(SseEmitter emitter, AiChatSession session, Long userId, UserMessage userMessage) {
        AtomicReference<String> conversationIdRef = new AtomicReference<>(session.getConversationId());
        AtomicReference<String> completedAnswerRef = new AtomicReference<>("");
        StringBuilder answerBuilder = new StringBuilder();
        StringBuilder chunkBuffer = new StringBuilder();

        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(cozeConfig.getBaseUrl())
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + cozeConfig.getAccessToken())
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE)
                    .build();

            Map<String, Object> requestBody = buildCozeRequestBody(userId, userMessage);

            Flux<String> responseFlux = webClient.post()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/v3/chat");
                        if (StringUtils.hasText(session.getConversationId())) {
                            uriBuilder.queryParam("conversation_id", session.getConversationId());
                        }
                        return uriBuilder.build();
                    })
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(DataBuffer.class)
                    .map(this::toUtf8String);

            responseFlux.doOnNext(chunk -> processSseChunk(
                            chunk,
                            chunkBuffer,
                            emitter,
                            session,
                            conversationIdRef,
                            answerBuilder,
                            completedAnswerRef
                    ))
                    .doOnError(error -> handleStreamError(emitter, session.getId(), error))
                    .doOnComplete(() -> finishStream(emitter, session, userId, conversationIdRef.get(),
                            answerBuilder.toString(), completedAnswerRef.get()))
                    .blockLast();
        } catch (Exception e) {
            handleStreamError(emitter, session.getId(), e);
        }
    }

    /**
     * Coze 的 SSE 响应是分块返回的，这里先把零散 chunk 拼起来，
     * 再按 SSE 事件分隔符进行切分。
     */
    private void processSseChunk(String chunk,
                                 StringBuilder chunkBuffer,
                                 SseEmitter emitter,
                                 AiChatSession session,
                                 AtomicReference<String> conversationIdRef,
                                 StringBuilder answerBuilder,
                                 AtomicReference<String> completedAnswerRef) {
        if (!StringUtils.hasText(chunk)) {
            return;
        }

        chunkBuffer.append(chunk.replace("\r\n", "\n"));

        int boundaryIndex;
        while ((boundaryIndex = chunkBuffer.indexOf("\n\n")) >= 0) {
            String eventBlock = chunkBuffer.substring(0, boundaryIndex);
            chunkBuffer.delete(0, boundaryIndex + 2);
            handleSseEvent(eventBlock, emitter, session, conversationIdRef, answerBuilder, completedAnswerRef);
        }
    }

    /**
     * 解析单个 SSE 事件：
     * 1. 提取 Coze conversation_id 并回写本地会话
     * 2. 转发增量回答给前端
     * 3. 记录 completed 事件中的完整回答，作为最终落库内容
     */
    private void handleSseEvent(String eventBlock,
                                SseEmitter emitter,
                                AiChatSession session,
                                AtomicReference<String> conversationIdRef,
                                StringBuilder answerBuilder,
                                AtomicReference<String> completedAnswerRef) {
        String eventName = null;
        StringBuilder dataBuilder = new StringBuilder();

        for (String rawLine : eventBlock.split("\n")) {
            if (!StringUtils.hasText(rawLine)) {
                continue;
            }
            if (rawLine.startsWith("event:")) {
                eventName = rawLine.substring(6).trim();
            } else if (rawLine.startsWith("data:")) {
                if (dataBuilder.length() > 0) {
                    dataBuilder.append('\n');
                }
                String dataLine = rawLine.substring(5);
                if (dataLine.startsWith(" ")) {
                    dataLine = dataLine.substring(1);
                }
                dataBuilder.append(dataLine);
            }
        }

        if (!StringUtils.hasText(eventName)) {
            return;
        }

        String data = dataBuilder.toString();
        if ("done".equals(eventName) || "\"[DONE]\"".equals(data) || "[DONE]".equals(data)) {
            return;
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(data);
            updateConversationIdIfNecessary(session, conversationIdRef, jsonNode, emitter);

            if ("conversation.message.delta".equals(eventName)
                    && "assistant".equals(jsonNode.path("role").asText())
                    && "answer".equals(jsonNode.path("type").asText())) {
                String content = jsonNode.path("content").asText("");
                if (content != null && !content.isEmpty()) {
                    answerBuilder.append(content);
                    Map<String, Object> messagePayload = new LinkedHashMap<>();
                    messagePayload.put("content", content);
                    emitter.send(SseEmitter.event()
                            .name("message")
                            .data(messagePayload));
                }
            }

            if ("conversation.message.completed".equals(eventName)
                    && "assistant".equals(jsonNode.path("role").asText())
                    && "answer".equals(jsonNode.path("type").asText())) {
                completedAnswerRef.set(jsonNode.path("content").asText(""));
            }
        } catch (Exception e) {
            log.warn("解析Coze SSE事件失败，event={}, data={}", eventName, data, e);
        }
    }

    /**
     * 首次从 Coze 响应里拿到 conversation_id 时，立即保存到本地会话，
     * 这样后续继续追问就可以沿用同一个 Coze 多轮会话。
     */
    private void updateConversationIdIfNecessary(AiChatSession session,
                                                 AtomicReference<String> conversationIdRef,
                                                 JsonNode jsonNode,
                                                 SseEmitter emitter) throws Exception {
        String conversationId = jsonNode.path("conversation_id").asText(null);
        if (!StringUtils.hasText(conversationId) || StringUtils.hasText(conversationIdRef.get())) {
            return;
        }

        conversationIdRef.set(conversationId);
        session.setConversationId(conversationId);
        session.setUpdatedAt(LocalDateTime.now());
        aiChatSessionMapper.updateById(session);

        Map<String, Object> sessionPayload = new LinkedHashMap<>();
        sessionPayload.put("sessionId", session.getId());
        sessionPayload.put("conversationId", conversationId);
        emitter.send(SseEmitter.event().name("session").data(sessionPayload));
    }

    /**
     * 流式结束时保存 assistant 最终答案，并向前端发送 done 事件。
     */
    private void finishStream(SseEmitter emitter,
                              AiChatSession session,
                              Long userId,
                              String conversationId,
                              String deltaAnswer,
                              String completedAnswer) {
        try {
            String finalAnswer = StringUtils.hasText(completedAnswer) ? completedAnswer : deltaAnswer;
            if (StringUtils.hasText(finalAnswer)) {
                saveAssistantMessage(session, userId, finalAnswer, conversationId);
            }

            Map<String, Object> donePayload = new LinkedHashMap<>();
            donePayload.put("sessionId", session.getId());
            donePayload.put("conversationId", conversationId);

            emitter.send(SseEmitter.event().name("done").data(donePayload));
            emitter.complete();
        } catch (Exception e) {
            handleStreamError(emitter, session.getId(), e);
        }
    }

    /**
     * 统一处理流式对话过程中的异常，并通过 SSE error 事件通知前端。
     */
    private void handleStreamError(SseEmitter emitter, Long sessionId, Throwable error) {
        log.error("小智流式对话失败，sessionId={}", sessionId, error);
        try {
            emitter.send(SseEmitter.event()
                    .name("error")
                    .data(error.getMessage() != null ? error.getMessage() : "小智对话失败"));
        } catch (Exception sendError) {
            log.error("发送SSE错误事件失败，sessionId={}", sessionId, sendError);
        }
        emitter.completeWithError(error);
    }

    /**
     * 构造 Coze /v3/chat 接口请求体。
     */
    private Map<String, Object> buildCozeRequestBody(Long userId, UserMessage userMessage) throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("bot_id", cozeConfig.getBotId());
        body.put("user_id", String.valueOf(userId));
        body.put("stream", true);
        body.put("auto_save_history", true);
        body.put("additional_messages", List.of(buildCozeAdditionalMessage(userMessage)));
        return body;
    }

    /**
     * 将 Spring AI 的用户消息转换为 Coze 所需的 additional_messages 结构。
     * 纯文本使用 text，图文场景使用 object_string。
     */
    private Map<String, Object> buildCozeAdditionalMessage(UserMessage userMessage) throws Exception {
        Map<String, Object> message = new LinkedHashMap<>();
        message.put("role", "user");

        if (userMessage.getMedia() == null || userMessage.getMedia().isEmpty()) {
            message.put("content", userMessage.getText());
            message.put("content_type", "text");
            return message;
        }

        List<Map<String, String>> contentParts = new ArrayList<>();
        for (Media media : userMessage.getMedia()) {
            Map<String, String> imagePart = new LinkedHashMap<>();
            imagePart.put("type", "image");
            imagePart.put("file_url", String.valueOf(media.getData()));
            contentParts.add(imagePart);
        }
        String messageText = StringUtils.hasText(userMessage.getText()) ? userMessage.getText() : DEFAULT_IMAGE_PROMPT;
        if (StringUtils.hasText(messageText)) {
            Map<String, String> textPart = new LinkedHashMap<>();
            textPart.put("type", "text");
            textPart.put("text", messageText);
            contentParts.add(textPart);
        }

        message.put("content", objectMapper.writeValueAsString(contentParts));
        message.put("content_type", "object_string");
        return message;
    }

    /**
     * 使用 Spring AI 的消息结构承载用户输入，
     * 方便统一处理文本问答和图文问答。
     */
    private UserMessage buildSpringAiUserMessage(String content, String imageUrl) {
        String text = StringUtils.hasText(content) ? content.trim() : "";
        if (!StringUtils.hasText(imageUrl)) {
            return UserMessage.builder().text(text).build();
        }

        Media media = new Media(MimeTypeUtils.parseMimeType("image/*"), URI.create(imageUrl));
        return UserMessage.builder()
                .text(text)
                .media(List.of(media))
                .build();
    }

    /**
     * 保存用户消息，并同步更新会话标题、预览、消息数和最后活跃时间。
     */
    private void saveUserMessage(AiChatSession session, Long userId, UserMessage userMessage) {
        LocalDateTime now = LocalDateTime.now();
        String preview = buildPreview(userMessage.getText(), !userMessage.getMedia().isEmpty());

        AiChatMessage message = new AiChatMessage();
        message.setSessionId(session.getId());
        message.setUserId(userId);
        message.setRole("user");
        message.setMessageType(resolveMessageType(userMessage.getText(), !userMessage.getMedia().isEmpty()));
        message.setSequenceNo(nextSequenceNo(session.getId()));
        message.setTextContent(StringUtils.hasText(userMessage.getText()) ? userMessage.getText() : null);
        message.setImageUrl(userMessage.getMedia().isEmpty() ? null : String.valueOf(userMessage.getMedia().get(0).getData()));
        message.setStatus(1);
        message.setCreatedAt(now);
        message.setUpdatedAt(now);
        aiChatMessageMapper.insert(message);

        if (session.getMessageCount() == null || session.getMessageCount() == 0 || "新对话".equals(session.getTitle())) {
            session.setTitle(buildSessionTitle(preview));
        }
        session.setLatestMessagePreview(preview);
        session.setMessageCount((session.getMessageCount() == null ? 0 : session.getMessageCount()) + 1);
        session.setLastMessageAt(now);
        session.setUpdatedAt(now);
        aiChatSessionMapper.updateById(session);
    }

    /**
     * 保存 assistant 最终回复，并更新会话的预览、消息数和 Coze conversation_id。
     */
    private void saveAssistantMessage(AiChatSession session, Long userId, String answer, String conversationId) {
        LocalDateTime now = LocalDateTime.now();

        AiChatMessage message = new AiChatMessage();
        message.setSessionId(session.getId());
        message.setUserId(userId);
        message.setRole("assistant");
        message.setMessageType("text");
        message.setSequenceNo(nextSequenceNo(session.getId()));
        message.setTextContent(answer);
        message.setImageUrl(null);
        message.setStatus(1);
        message.setCreatedAt(now);
        message.setUpdatedAt(now);
        aiChatMessageMapper.insert(message);

        session.setConversationId(conversationId);
        session.setLatestMessagePreview(buildPreview(answer, false));
        session.setMessageCount((session.getMessageCount() == null ? 0 : session.getMessageCount()) + 1);
        session.setLastMessageAt(now);
        session.setUpdatedAt(now);
        aiChatSessionMapper.updateById(session);
    }

    /**
     * 获取已有会话；如果前端未传 sessionId，则创建一个新的本地会话。
     */
    private AiChatSession getOrCreateSession(Long userId, Long sessionId) {
        if (sessionId != null) {
            return getSessionOrThrow(userId, sessionId);
        }

        LocalDateTime now = LocalDateTime.now();
        AiChatSession session = new AiChatSession();
        session.setUserId(userId);
        session.setConversationId(null);
        session.setTitle("新对话");
        session.setLatestMessagePreview(null);
        session.setMessageCount(0);
        session.setStatus(1);
        session.setLastMessageAt(now);
        session.setCreatedAt(now);
        session.setUpdatedAt(now);
        aiChatSessionMapper.insert(session);
        return session;
    }

    /**
     * 校验并获取当前用户的有效会话。
     */
    private AiChatSession getSessionOrThrow(Long userId, Long sessionId) {
        LambdaQueryWrapper<AiChatSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatSession::getId, sessionId)
                .eq(AiChatSession::getUserId, userId)
                .eq(AiChatSession::getStatus, 1)
                .last("LIMIT 1");

        AiChatSession session = aiChatSessionMapper.selectOne(wrapper);
        if (session == null) {
            throw new BusinessException(ResultCodeEnum.DATA_NOT_FOUND);
        }
        return session;
    }

    /**
     * 计算会话内下一条消息的顺序号，保证多轮消息按发送顺序展示。
     */
    private int nextSequenceNo(Long sessionId) {
        LambdaQueryWrapper<AiChatMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiChatMessage::getSessionId, sessionId)
                .orderByDesc(AiChatMessage::getSequenceNo)
                .last("LIMIT 1");

        AiChatMessage latestMessage = aiChatMessageMapper.selectOne(wrapper);
        return latestMessage == null ? 1 : latestMessage.getSequenceNo() + 1;
    }

    private String resolveMessageType(String text, boolean hasImage) {
        if (hasImage && StringUtils.hasText(text)) {
            return "text_image";
        }
        if (hasImage) {
            return "image";
        }
        return "text";
    }

    private String buildSessionTitle(String preview) {
        if (!StringUtils.hasText(preview)) {
            return "图片咨询";
        }
        return preview.length() > 20 ? preview.substring(0, 20) : preview;
    }

    private String buildPreview(String text, boolean hasImage) {
        if (StringUtils.hasText(text) && hasImage) {
            String mixed = text.trim() + " [图片]";
            return mixed.length() > 255 ? mixed.substring(0, 255) : mixed;
        }
        if (StringUtils.hasText(text)) {
            String trimmed = text.trim();
            return trimmed.length() > 255 ? trimmed.substring(0, 255) : trimmed;
        }
        return hasImage ? "[图片]" : "";
    }

    private String toUtf8String(DataBuffer dataBuffer) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private XiaozhiSessionVO toSessionVO(AiChatSession session) {
        XiaozhiSessionVO vo = new XiaozhiSessionVO();
        vo.setId(session.getId());
        vo.setTitle(session.getTitle());
        vo.setPreview(session.getLatestMessagePreview());
        vo.setMessageCount(session.getMessageCount());
        vo.setLastMessageAt(session.getLastMessageAt());
        return vo;
    }

    private XiaozhiMessageVO toMessageVO(AiChatMessage message) {
        XiaozhiMessageVO vo = new XiaozhiMessageVO();
        vo.setId(message.getId());
        vo.setRole(message.getRole());
        vo.setMessageType(message.getMessageType());
        vo.setContent(message.getTextContent());
        vo.setImageUrl(message.getImageUrl());
        vo.setCreatedAt(message.getCreatedAt());
        return vo;
    }
}
