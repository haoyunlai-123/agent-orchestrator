package com.my.agent.planner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.agent.llm.LlmClient;
import com.my.agent.model.plan.ExecutionPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LlmPlanFactory {

    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;

    public ExecutionPlan createPlan(String goal) {
        try {
            String planJson = llmClient.generatePlanJson(goal);
            return objectMapper.readValue(planJson, ExecutionPlan.class);
        } catch (Exception e) {
            throw new RuntimeException("解析 LLM 计划失败: " + e.getMessage(), e);
        }
    }
}