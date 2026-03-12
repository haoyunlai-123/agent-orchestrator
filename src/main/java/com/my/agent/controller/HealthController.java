package com.my.agent.controller;

import com.my.agent.model.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.success(Map.of(
                "service", "agent-orchestrator",
                "status", "UP",
                "time", LocalDateTime.now().toString()
        ));
    }
}