package com.recipe.srback.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe.srback.config.CozeConfig;
import com.recipe.srback.exception.BusinessException;
import com.recipe.srback.result.ResultCodeEnum;
import com.recipe.srback.service.FoodCheckinService;
import com.recipe.srback.vo.FoodCheckinVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 食物打卡服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FoodCheckinServiceImpl implements FoodCheckinService {
    
    private final CozeConfig cozeConfig;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
    
    @Override
    public FoodCheckinVO analyzeFoodImage(String imageUrl, String mealType) {
        try {
            // 1. 构建请求参数
            Map<String, Object> request = buildRequest(imageUrl, mealType);
            
            log.info("调用Coze工作流，请求参数：{}", objectMapper.writeValueAsString(request));
            
            // 2. 调用Coze工作流API
            WebClient webClient = webClientBuilder
                    .baseUrl(cozeConfig.getBaseUrl())
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + cozeConfig.getAccessToken())
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();
            
            String responseBody = webClient.post()
                    .uri("/v1/workflow/run")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60))
                    .block();
            
            log.info("Coze工作流响应：{}", responseBody);
            
            // 3. 解析响应
            JsonNode responseJson = objectMapper.readTree(responseBody);
            
            // 检查响应状态
            int code = responseJson.path("code").asInt(-1);
            if (code != 0) {
                String msg = responseJson.path("msg").asText("调用Coze工作流失败");
                log.error("Coze工作流返回错误，code：{}，msg：{}", code, msg);
                throw new BusinessException(ResultCodeEnum.AI_SERVICE_ERROR);
            }
            
            // 获取data字段（字符串类型）
            String dataStr = responseJson.path("data").asText();
            if (dataStr == null || dataStr.isEmpty()) {
                log.error("Coze工作流返回的data字段为空");
                throw new BusinessException(ResultCodeEnum.AI_SERVICE_ERROR);
            }
            
            log.info("Coze data字段：{}", dataStr);
            
            // 解析data字段为JSON对象
            JsonNode dataJson = objectMapper.readTree(dataStr);
            
            // 获取output字段（可能是字符串，包含markdown代码块）
            String outputStr = dataJson.path("output").asText();
            if (outputStr == null || outputStr.isEmpty()) {
                log.error("Coze工作流返回的output字段为空");
                throw new BusinessException(ResultCodeEnum.AI_SERVICE_ERROR);
            }
            
            log.info("Coze output字段（原始）：{}", outputStr);
            
            // 去除markdown代码块标记
            outputStr = outputStr.trim();
            if (outputStr.startsWith("```json")) {
                outputStr = outputStr.substring(7); // 去除 ```json
            }
            if (outputStr.startsWith("```")) {
                outputStr = outputStr.substring(3); // 去除 ```
            }
            if (outputStr.endsWith("```")) {
                outputStr = outputStr.substring(0, outputStr.length() - 3); // 去除结尾的 ```
            }
            outputStr = outputStr.trim();
            
            log.info("Coze output字段（清理后）：{}", outputStr);
            
            // 解析为JSON对象
            JsonNode outputJson = objectMapper.readTree(outputStr);
            
            // 4. 转换为VO对象
            FoodCheckinVO vo = new FoodCheckinVO();
            vo.setFoodName(outputJson.path("food_name").asText());
            vo.setCalories(outputJson.path("calories").asInt());
            vo.setProtein(outputJson.path("protein").asDouble());
            vo.setCarbs(outputJson.path("carbs").asDouble());
            vo.setFat(outputJson.path("fat").asDouble());
            vo.setAmount(outputJson.path("amount").asText());
            vo.setConfidence(outputJson.path("confidence").asDouble());
            
            // 验证数据有效性（即使识别失败也返回结果，让前端处理）
            if (vo.getFoodName() == null || vo.getFoodName().isEmpty()) {
                log.warn("Coze工作流返回的食物名称为空");
                vo.setFoodName("识别失败");
                vo.setConfidence(0.0);
            }
            
            log.info("食物识别完成：{}，置信度：{}", vo.getFoodName(), vo.getConfidence());
            
            return vo;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用Coze工作流异常", e);
            throw new BusinessException(ResultCodeEnum.AI_SERVICE_ERROR);
        }
    }
    
    /**
     * 构建Coze工作流请求参数
     */
    private Map<String, Object> buildRequest(String imageUrl, String mealType) {
        Map<String, Object> request = new HashMap<>();
        
        // 工作流ID
        request.put("workflow_id", "7617386908322889774");
        
        // 参数（使用驼峰命名，与Coze工作流配置一致）
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("imageUrl", imageUrl);
        parameters.put("mealType", mealType);
        parameters.put("input", "打卡");
        
        request.put("parameters", parameters);
        
        return request;
    }
}
