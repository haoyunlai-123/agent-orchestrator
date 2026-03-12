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
public class GetInstanceResultTool implements AgentTool {

    private final SchedulerGatewayClient schedulerGatewayClient;

    @Override
    public String name() {
        return "get_instance_result";
    }

    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        Object instanceIdObj = params.get("instanceId");
        if (instanceIdObj == null) {
            return ToolResult.fail("缺少参数 instanceId");
        }

        Long instanceId = Long.valueOf(instanceIdObj.toString());
        Map<String, Object> result = schedulerGatewayClient.queryInstanceResult(instanceId);
        return ToolResult.success("获取任务结果成功", result);
    }
}