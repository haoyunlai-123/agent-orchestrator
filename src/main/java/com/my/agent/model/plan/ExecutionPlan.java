package com.my.agent.model.plan;

import lombok.Data;

import java.util.List;

@Data
public class ExecutionPlan {

    private String goal;
    private List<PlanStep> steps;
}