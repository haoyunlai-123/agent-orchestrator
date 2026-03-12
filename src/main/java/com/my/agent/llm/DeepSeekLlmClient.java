package com.my.agent.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.agent.config.LlmProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DeepSeekLlmClient implements LlmClient {

    private final RestClient restClient;
    private final LlmProperties llmProperties;
    private final ObjectMapper objectMapper;

    @Override
    public String generatePlanJson(String userGoal) {
        if (!StringUtils.hasText(llmProperties.getApiKey())) {
            throw new IllegalStateException("未配置 DeepSeek API Key，请设置环境变量 DEEPSEEK_API_KEY");
        }

        String systemPrompt = """
                你是一个任务编排规划器。
                你只能输出 JSON，不能输出 markdown。
                你需要根据用户目标生成执行计划。
                计划格式为：
                {
                  "goal": "xxx",
                  "steps": [
                    {
                      "stepId": "s1",
                      "action": "create_job",
                      "dependsOn": [],
                      "params": {
                        "name": "demo-http-job",
                        "scheduleType": "FIXED_RATE",
                        "scheduleExpr": "5000",
                        "handlerType": "HTTP",
                        "handlerParam": "{\\"url\\":\\"http://127.0.0.1:9002/health\\",\\"method\\":\\"GET\\"}",
                        "routeStrategy": "ROUND_ROBIN",
                        "retryMax": 1,
                        "timeoutMs": 3000,
                        "enabled": false
                      }
                    },
                    {
                      "stepId": "s2",
                      "action": "trigger_job",
                      "dependsOn": ["s1"],
                      "params": {
                        "jobId": "${s1.jobId}"
                      }
                    }
                  ]
                }
                action 只能从 create_job、trigger_job、wait_instance、get_instance_result 中选择。
                必须输出合法 json。
                """;

        try {
            Map<String, Object> body = Map.of(
                    "model", llmProperties.getModel(),
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", "请根据下面目标生成 json 计划: " + userGoal)
                    ),
                    "response_format", Map.of("type", "json_object"),
                    "stream", false
            );

            String raw = restClient.post()
                    .uri(llmProperties.getBaseUrl() + "/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + llmProperties.getApiKey())
                    .body(body)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(raw);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            throw new RuntimeException("调用 DeepSeek 生成计划失败: " + e.getMessage(), e);
        }
    }
}