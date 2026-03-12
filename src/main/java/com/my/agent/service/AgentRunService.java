package com.my.agent.service;

import com.my.agent.common.enums.RunStatus;
import com.my.agent.model.plan.ExecutionPlan;
import com.my.agent.model.request.AgentRunRequest;
import com.my.agent.model.response.AgentRunResponse;
import com.my.agent.planner.FixedPlanFactory;
import com.my.agent.planner.LlmPlanFactory;
import com.my.agent.runtime.AgentRunContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgentRunService {

    private final FixedPlanFactory fixedPlanFactory;
    private final LlmPlanFactory llmPlanFactory;
    private final PlanExecutor planExecutor;

    public AgentRunResponse run(AgentRunRequest request) {
        String runId = UUID.randomUUID().toString();

        ExecutionPlan plan = "llm".equalsIgnoreCase(request.getMode())
                ? llmPlanFactory.createPlan(request.getGoal())
                : fixedPlanFactory.createFixedPlan(request.getGoal());

        AgentRunContext context = new AgentRunContext();
        context.setRunId(runId);
        context.setGoal(request.getGoal());
        context.setStatus(RunStatus.RUNNING);

        AgentRunContext finalContext = planExecutor.execute(plan, context);

        return AgentRunResponse.builder()
                .runId(runId)
                .goal(request.getGoal())
                .status(finalContext.getStatus().name())
                .summary(buildSummary(finalContext))
                .plan(plan)
                .stepResults(finalContext.getStepResults().stream()
                        .map(step -> java.util.Map.of(
                                "stepId", step.getStepId(),
                                "action", step.getAction(),
                                "status", step.getStatus().name(),
                                "message", step.getMessage(),
                                "output", step.getOutput() == null ? java.util.Map.of() : step.getOutput()
                        ))
                        .toList())
                .build();
    }

    private String buildSummary(AgentRunContext context) {
        if (context.getStatus() == RunStatus.SUCCESS) {
            return "执行成功，共完成 " + context.getStepResults().size() + " 个步骤";
        }
        return "执行失败";
    }
}