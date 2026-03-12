package com.my.agent.model.plan;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PlanStep {

    private String stepId;
    private String action;
    private List<String> dependsOn;
    private Map<String, Object> params;
}