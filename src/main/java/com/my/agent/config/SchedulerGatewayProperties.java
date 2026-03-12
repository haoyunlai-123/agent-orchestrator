package com.my.agent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "scheduler.gateway")
public class SchedulerGatewayProperties {

    private String baseUrl;
    private Integer connectTimeoutMs;
    private Integer readTimeoutMs;
}