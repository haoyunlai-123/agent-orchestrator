package com.my.agent.model.response;

import com.my.agent.model.plan.ExecutionPlan;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class AgentRunResponse {

    private String runId;
    private String goal;
    private String status;
    private String summary;
    private ExecutionPlan plan;
    private List<Map<String, Object>> stepResults;
}