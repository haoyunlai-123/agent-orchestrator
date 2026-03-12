package com.my.agent.runtime;

import com.my.agent.common.enums.StepStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class StepExecutionResult {

    private String stepId;
    private String action;
    private StepStatus status;
    private String message;
    private Map<String, Object> output;
}