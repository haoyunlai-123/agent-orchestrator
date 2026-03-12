package com.my.agent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "llm")
public class LlmProperties {

    private String provider;
    private String baseUrl;
    private String model;
    private String apiKey;
    private Integer connectTimeoutMs;
    private Integer readTimeoutMs;
}