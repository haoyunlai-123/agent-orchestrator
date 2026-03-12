package com.my.agent.planner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.agent.model.plan.ExecutionPlan;
import com.my.agent.model.plan.PlanStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FixedPlanFactory {

    private final ObjectMapper objectMapper;

    public ExecutionPlan createFixedPlan(String goal) {
        ExecutionPlan plan = new ExecutionPlan();
        plan.setGoal(goal);

        String handlerParam = buildHandlerParam();

        PlanStep step1 = new PlanStep();
        step1.setStepId("s1");
        step1.setAction("create_job");
        step1.setDependsOn(List.of());
        step1.setParams(Map.of(
                "name", "demo-http-job",
                "scheduleType", "FIXED_RATE",
                "scheduleExpr", "5000",
                "handlerType", "HTTP",
                "handlerParam", handlerParam,
                "routeStrategy", "ROUND_ROBIN",
                "retryMax", 1,
                "timeoutMs", 3000,
                "enabled", false
        ));

        PlanStep step2 = new PlanStep();
        step2.setStepId("s2");
        step2.setAction("trigger_job");
        step2.setDependsOn(List.of("s1"));
        step2.setParams(Map.of(
                "jobId", "${s1.jobId}"
        ));

        PlanStep step3 = new PlanStep();
        step3.setStepId("s3");
        step3.setAction("wait_instance");
        step3.setDependsOn(List.of("s2"));
        step3.setParams(Map.of(
                "instanceId", "${s2.instanceId}"
        ));

        PlanStep step4 = new PlanStep();
        step4.setStepId("s4");
        step4.setAction("get_instance_result");
        step4.setDependsOn(List.of("s3"));
        step4.setParams(Map.of(
                "instanceId", "${s2.instanceId}"
        ));

        plan.setSteps(List.of(step1, step2, step3, step4));
        return plan;
    }

    private String buildHandlerParam() {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "url", "http://127.0.0.1:9002/health",
                    "method", "GET"
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("构造 handlerParam 失败", e);
        }
    }
}