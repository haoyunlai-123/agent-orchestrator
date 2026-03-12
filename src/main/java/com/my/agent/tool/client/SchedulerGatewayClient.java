package com.my.agent.tool.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.agent.config.SchedulerGatewayProperties;
import com.my.agent.model.request.CreateJobCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SchedulerGatewayClient {

    private final RestClient restClient;
    private final SchedulerGatewayProperties properties;
    private final ObjectMapper objectMapper;

    public Map<String, Object> createJob(Map<String, Object> params) {
        try {
            CreateJobCommand command = CreateJobCommand.builder()
                    .name(String.valueOf(params.getOrDefault("name", "agent-http-job")))
                    .scheduleType(String.valueOf(params.getOrDefault("scheduleType", "FIXED_RATE")))
                    .scheduleExpr(String.valueOf(params.getOrDefault("scheduleExpr", "5000")))
                    .handlerType(String.valueOf(params.getOrDefault("handlerType", "HTTP")))
                    .handlerParam(String.valueOf(params.get("handlerParam")))
                    .routeStrategy(String.valueOf(params.getOrDefault("routeStrategy", "ROUND_ROBIN")))
                    .retryMax(Integer.parseInt(String.valueOf(params.getOrDefault("retryMax", 1))))
                    .timeoutMs(Integer.parseInt(String.valueOf(params.getOrDefault("timeoutMs", 3000))))
                    .enabled(Boolean.parseBoolean(String.valueOf(params.getOrDefault("enabled", false))))
                    .build();

            String raw = restClient.post()
                    .uri(properties.getBaseUrl() + "/api/jobs")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(command)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(raw);
            JsonNode data = root.has("data") ? root.get("data") : root;

            Long jobId = null;
            if (data.has("jobId")) {
                jobId = data.get("jobId").asLong();
            } else if (data.has("id")) {
                jobId = data.get("id").asLong();
            }

            Map<String, Object> result = new HashMap<>();
            result.put("jobId", jobId);
            result.put("rawResponse", raw);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("调用创建任务接口失败: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> triggerJob(Long jobId) {
        try {
            String raw = restClient.post()
                    .uri(properties.getBaseUrl() + "/api/jobs/{jobId}/trigger", jobId)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(raw);
            JsonNode data = root.has("data") ? root.get("data") : root;

            Long instanceId = null;
            if (data.has("instanceId")) {
                instanceId = data.get("instanceId").asLong();
            } else if (data.has("id")) {
                instanceId = data.get("id").asLong();
            }

            Map<String, Object> result = new HashMap<>();
            result.put("jobId", jobId);
            result.put("instanceId", instanceId);
            result.put("rawResponse", raw);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("调用触发任务接口失败: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> queryInstanceStatus(Long instanceId) {
        // 这里先占位，等给出真实实例查询接口后再精确改
        Map<String, Object> result = new HashMap<>();
        result.put("instanceId", instanceId);
        result.put("status", "SUCCESS");
        return result;
    }

    public Map<String, Object> queryInstanceResult(Long instanceId) {
        Map<String, Object> result = new HashMap<>();
        result.put("instanceId", instanceId);
        result.put("result", "等待你补充真实任务结果查询接口");
        return result;
    }
}