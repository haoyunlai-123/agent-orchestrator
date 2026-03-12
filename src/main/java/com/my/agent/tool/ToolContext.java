package com.my.agent.tool;

import com.my.agent.runtime.AgentRunContext;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ToolContext {

    private AgentRunContext runContext;
}