package com.my.agent.tool;

import java.util.Map;

public interface AgentTool {

    String name();

    ToolResult execute(Map<String, Object> params, ToolContext context);
}