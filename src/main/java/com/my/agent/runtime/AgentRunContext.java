package com.my.agent.runtime;

import com.my.agent.common.enums.RunStatus;
import lombok.Data;

import java.util.*;

@Data
public class AgentRunContext {

    private String runId;
    private String goal;
    private RunStatus status;

    /**
     * 每个 step 的输出结果
     * key = stepId
     * value = output map
     */
    private Map<String, Map<String, Object>> stepOutputs = new HashMap<>();

    /**
     * 每一步的执行记录
     */
    private List<StepExecutionResult> stepResults = new ArrayList<>();

    public void addStepOutput(String stepId, Map<String, Object> output) {
        if (output == null) {
            output = Collections.emptyMap();
        }
        stepOutputs.put(stepId, output);
    }

    public Map<String, Object> getStepOutput(String stepId) {
        return stepOutputs.get(stepId);
    }

    public void addStepResult(StepExecutionResult result) {
        stepResults.add(result);
    }
}