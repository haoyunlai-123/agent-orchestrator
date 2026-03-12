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
public class TriggerJobTool implements AgentTool {

    private final SchedulerGatewayClient schedulerGatewayClient;

    @Override
    public String name() {
        return "trigger_job";
    }

    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        Object jobIdObj = params.get("jobId");
        if (jobIdObj == null) {
            return ToolResult.fail("缺少参数 jobId");
        }

        Long jobId = Long.valueOf(jobIdObj.toString());
        Map<String, Object> result = schedulerGatewayClient.triggerJob(jobId);
        return ToolResult.success("任务触发成功", result);
    }
}