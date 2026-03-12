package com.my.agent.tool.impl;

import com.my.agent.tool.AgentTool;
import com.my.agent.tool.ToolContext;
import com.my.agent.tool.ToolResult;
import com.my.agent.tool.client.SchedulerGatewayClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CreateJobTool implements AgentTool {

    private final SchedulerGatewayClient schedulerGatewayClient;

    @Override
    public String name() {
        return "create_job";
    }

    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        Map<String, Object> result = schedulerGatewayClient.createJob(params);
        return ToolResult.success("任务创建成功", result);
    }
}