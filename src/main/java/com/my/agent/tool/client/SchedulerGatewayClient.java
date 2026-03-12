package com.my.agent.tool.client;

import com.my.agent.config.SchedulerGatewayProperties;
import com.my.agent.tool.client.dto.SchedulerApiResponse;
import com.my.agent.tool.client.dto.TriggerOnceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SchedulerGatewayClient {

    private final RestTemplate restTemplate;
    private final SchedulerGatewayProperties properties;

    public Map<String, Object> createJob(Map<String, Object> params) {
        String url = properties.getBaseUrl() + "/api/jobs";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);

        ResponseEntity<SchedulerApiResponse<Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        SchedulerApiResponse<Object> body = response.getBody();
        if (body == null) {
            throw new IllegalStateException("createJob 响应为空");
        }
        if (body.getCode() == null || body.getCode() != 0) {
            throw new IllegalStateException("createJob 失败: " + body.getMsg());
        }

        Object data = body.getData();
        if (data == null) {
            throw new IllegalStateException("createJob 成功但 data 为空");
        }

        Long jobId = Long.valueOf(data.toString());

        Map<String, Object> result = new HashMap<>();
        result.put("jobId", jobId);
        result.put("rawData", data);
        result.put("rawMsg", body.getMsg());
        return result;
    }

    public Map<String, Object> triggerJob(Long jobId) {
        String url = properties.getBaseUrl() + "/api/jobs/" + jobId + "/trigger";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<SchedulerApiResponse<TriggerOnceResponse>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        SchedulerApiResponse<TriggerOnceResponse> body = response.getBody();
        if (body == null) {
            throw new IllegalStateException("triggerJob 响应为空");
        }
        if (body.getCode() == null || body.getCode() != 0) {
            throw new IllegalStateException("triggerJob 失败: " + body.getMsg());
        }
        if (body.getData() == null) {
            throw new IllegalStateException("triggerJob 成功但 data 为空");
        }

        TriggerOnceResponse data = body.getData();
        if (data.getInstanceId() == null) {
            throw new IllegalStateException("triggerJob 成功但 instanceId 为空");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("instanceId", data.getInstanceId());
        result.put("result", data.isResult());
        result.put("jobId", jobId);
        return result;
    }

    public Map<String, Object> queryInstanceStatus(Long instanceId) {
        // 占位实现：后续替换成真实接口
        Map<String, Object> result = new HashMap<>();
        result.put("instanceId", instanceId);
        result.put("status", "SUCCESS");
        return result;
    }

    public Map<String, Object> queryInstanceResult(Long instanceId) {
        // 占位实现：后续替换成真实接口
        Map<String, Object> result = new HashMap<>();
        result.put("instanceId", instanceId);
        result.put("result", "等待接入真实任务结果查询接口");
        return result;
    }
}