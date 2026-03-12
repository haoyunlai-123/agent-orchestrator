package com.my.agent.service;

import com.my.agent.model.plan.ExecutionPlan;
import com.my.agent.model.request.AgentRunRequest;
import com.my.agent.model.response.AgentRunResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AgentRunService {

    public AgentRunResponse run(AgentRunRequest request) {
        String runId = UUID.randomUUID().toString();

        ExecutionPlan mockPlan = new ExecutionPlan();
        mockPlan.setGoal(request.getGoal());
        mockPlan.setSteps(List.of());

        return AgentRunResponse.builder()
                .runId(runId)
                .goal(request.getGoal())
                .status("SUCCESS")
                .summary("第一步工程骨架已完成，当前返回的是占位响应，后续将接入 Planner 与 Tool 执行链路")
                .plan(mockPlan)
                .stepResults(List.of(
                        Map.of("step", "INIT", "status", "SUCCESS", "message", "project skeleton ready")
                ))
                .build();
    }
}
