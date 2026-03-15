package com.recipe.srback.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe.srback.config.CozeConfig;
import com.recipe.srback.service.RecipeAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 食谱分析服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeAnalysisServiceImpl implements RecipeAnalysisService {
    
    private final CozeConfig cozeConfig;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    @Override
    public SseEmitter analyzeRecipeStream(String imageUrl, String input) {
        // 创建SSE发射器，设置超时时间为5分钟
        SseEmitter emitter = new SseEmitter(300000L);
        
        // 异步处理流式响应
        executor.execute(() -> {
            try {
                // 构建请求体
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("workflow_id", "7617449628535488562");
                
                Map<String, String> parameters = new HashMap<>();
                parameters.put("imageUrl", imageUrl);
                parameters.put("input", input != null ? input : "分析食谱");
                requestBody.put("parameters", parameters);
                
                log.info("调用Coze食谱分析工作流，imageUrl：{}，input：{}", imageUrl, input);
                
                // 创建WebClient
                WebClient webClient = webClientBuilder
                        .baseUrl(cozeConfig.getBaseUrl())
                        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + cozeConfig.getAccessToken())
                        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .build();
                
                // 调用流式API
                Flux<String> responseFlux = webClient.post()
                        .uri("/v1/workflow/stream_run")
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToFlux(org.springframework.core.io.buffer.DataBuffer.class)
                        .map(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            org.springframework.core.io.buffer.DataBufferUtils.release(dataBuffer);
                            return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
                        });
                
                // 直接转发Coze的原始SSE数据
                responseFlux.subscribe(
                        chunk -> {
                            try {
                                // 跳过空块
                                if (chunk == null || chunk.trim().isEmpty()) {
                                    return;
                                }
                                
                                log.debug("转发数据块：{}", chunk);
                                
                                // 直接发送原始SSE数据到前端
                                emitter.send(chunk);
                                
                            } catch (Exception e) {
                                log.error("转发流式响应失败", e);
                                try {
                                    emitter.completeWithError(e);
                                } catch (Exception ex) {
                                    log.error("发送错误失败", ex);
                                }
                            }
                        },
                        error -> {
                            log.error("调用Coze API失败", error);
                            try {
                                emitter.completeWithError(error);
                            } catch (Exception e) {
                                log.error("发送错误失败", e);
                            }
                        },
                        () -> {
                            log.info("流式响应转发完成");
                            emitter.complete();
                        }
                );
                
            } catch (Exception e) {
                log.error("启动流式分析失败", e);
                try {
                    emitter.completeWithError(e);
                } catch (Exception ex) {
                    log.error("发送错误失败", ex);
                }
            }
        });
        
        // 设置超时和错误回调
        emitter.onTimeout(() -> {
            log.warn("SSE连接超时");
            emitter.complete();
        });
        
        emitter.onError(e -> {
            log.error("SSE连接错误", e);
        });
        
        return emitter;
    }
}
