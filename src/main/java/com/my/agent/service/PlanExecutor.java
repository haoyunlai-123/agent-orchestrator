package com.my.agent.service;

import com.my.agent.common.enums.RunStatus;
import com.my.agent.common.enums.StepStatus;
import com.my.agent.model.plan.ExecutionPlan;
import com.my.agent.model.plan.PlanStep;
import com.my.agent.runtime.AgentRunContext;
import com.my.agent.runtime.StepExecutionResult;
import com.my.agent.tool.AgentTool;
import com.my.agent.tool.AgentToolRegistry;
import com.my.agent.tool.ToolContext;
import com.my.agent.tool.ToolResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 *PlanExecutor 是一个核心的服务类，负责执行由多个步骤组成的执行计划（ExecutionPlan）。
 *它按照计划中步骤的顺序，处理步骤间的依赖关系、参数解析、工具调用，并维护整个运行过程中的上下文状态。其核心逻辑如下：
 *初始化运行状态：将传入的 AgentRunContext 状态置为 RUNNING。
 *遍历执行步骤：依次处理计划中的每个 PlanStep。
 *依赖检查：通过 checkDependsOn 确保当前步骤所依赖的所有前置步骤（由 dependsOn 字段指定）已经在上下文中产生了输出，否则抛出异常。
 *参数解析：调用 resolveParams 解析步骤参数。如果参数值是 ${stepId.field} 形式的占位符，则从上下文中提取对应步骤的输出字段进行替换，实现步骤间数据传递。
 *获取并执行工具：根据步骤的 action 从 AgentToolRegistry 获取对应的 AgentTool，传入解析后的参数和工具上下文（包含 runContext）执行。
 *处理执行结果：
 *若工具执行失败（result.isSuccess() == false），记录失败步骤结果，将运行状态置为 FAILED 并立即返回。
 *若成功，将工具的输出存入上下文（addStepOutput），并记录成功步骤结果。
 *异常捕获：任何执行过程中的异常都会导致当前步骤标记为失败，并终止整个计划运行，返回失败的上下文。
 *完成执行：所有步骤成功后，将运行状态置为 SUCCESS，返回更新后的上下文。
 *PlanExecutor 是 DAG 任务编排的执行引擎，它通过顺序遍历步骤并依赖上下文中的输出数据来保证依赖关系的正确性，
 * 同时支持动态参数替换，使得步骤之间可以灵活传递数据。该类的设计简化了多步骤任务的协调，将工具调用、状态管理和数据流整合在一起，
 * 是 Agent 执行计划的核心组件。
 *
 */
@Service
@RequiredArgsConstructor
public class PlanExecutor {

    private final AgentToolRegistry agentToolRegistry;

    public AgentRunContext execute(ExecutionPlan plan, AgentRunContext context) {
        context.setStatus(RunStatus.RUNNING);

        for (PlanStep step : plan.getSteps()) {
            try {
                checkDependsOn(step, context);

                Map<String, Object> resolvedParams = resolveParams(step.getParams(), context);

                AgentTool tool = agentToolRegistry.getTool(step.getAction());
                ToolResult result = tool.execute(
                        resolvedParams,
                        ToolContext.builder().runContext(context).build()
                );

                if (!result.isSuccess()) {
                    context.addStepResult(
                            StepExecutionResult.builder()
                                    .stepId(step.getStepId())
                                    .action(step.getAction())
                                    .status(StepStatus.FAILED)
                                    .message(result.getMessage())
                                    .output(result.getOutput())
                                    .build()
                    );
                    context.setStatus(RunStatus.FAILED);
                    return context;
                }

                context.addStepOutput(step.getStepId(), result.getOutput());
                context.addStepResult(
                        StepExecutionResult.builder()
                                .stepId(step.getStepId())
                                .action(step.getAction())
                                .status(StepStatus.SUCCESS)
                                .message(result.getMessage())
                                .output(result.getOutput())
                                .build()
                );

            } catch (Exception e) {
                context.addStepResult(
                        StepExecutionResult.builder()
                                .stepId(step.getStepId())
                                .action(step.getAction())
                                .status(StepStatus.FAILED)
                                .message("执行异常: " + e.getMessage())
                                .output(Map.of())
                                .build()
                );
                context.setStatus(RunStatus.FAILED);
                return context;
            }
        }

        context.setStatus(RunStatus.SUCCESS);
        return context;
    }

    private void checkDependsOn(PlanStep step, AgentRunContext context) {
        List<String> dependsOn = step.getDependsOn();
        if (dependsOn == null || dependsOn.isEmpty()) {
            return;
        }

        for (String dep : dependsOn) {
            if (context.getStepOutput(dep) == null) {
                throw new IllegalStateException("依赖步骤尚未完成: " + dep);
            }
        }
    }

    private Map<String, Object> resolveParams(Map<String, Object> params, AgentRunContext context) {
        Map<String, Object> resolved = new HashMap<>();
        if (params == null) {
            return resolved;
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String str) {
                resolved.put(entry.getKey(), resolveVariable(str, context));
            } else {
                resolved.put(entry.getKey(), value);
            }
        }
        return resolved;
    }

    private Object resolveVariable(String value, AgentRunContext context) {
        if (!value.startsWith("${") || !value.endsWith("}")) {
            return value;
        }

        String expr = value.substring(2, value.length() - 1); // s1.jobId
        String[] parts = expr.split("\\.");
        if (parts.length != 2) {
            return value;
        }

        String stepId = parts[0];
        String field = parts[1];

        Map<String, Object> stepOutput = context.getStepOutput(stepId);
        if (stepOutput == null) {
            throw new IllegalStateException("找不到 step 输出: " + stepId);
        }

        Object fieldValue = stepOutput.get(field);
        if (fieldValue == null) {
            throw new IllegalStateException("step 输出中找不到字段: " + expr);
        }

        return fieldValue;
    }
}