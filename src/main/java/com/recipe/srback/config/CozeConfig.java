package com.recipe.srback.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Coze配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "coze")
public class CozeConfig {
    
    private String baseUrl;
    
    private String accessToken;
    
    private String workflowId;

    private String botId;
    
    private Integer timeout = 30000;
    
    /**
     * 配置WebClient Bean
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
