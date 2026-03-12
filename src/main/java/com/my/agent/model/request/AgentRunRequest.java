package com.my.agent.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AgentRunRequest {

    @NotBlank(message = "goal 不能为空")
    private String goal;

    /**
     * fixed / llm
     */
    private String mode = "fixed";
}