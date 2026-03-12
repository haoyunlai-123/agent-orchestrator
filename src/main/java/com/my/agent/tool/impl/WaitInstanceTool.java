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
public class WaitInstanceTool implements AgentTool {

    private final SchedulerGatewayClient schedulerGatewayClient;

    @Override
    public String name() {
        return "wait_instance";
    }

    @Override
    public ToolResult execute(Map<String, Object> params, ToolContext context) {
        Object instanceIdObj = params.get("instanceId");
        if (instanceIdObj == null) {
            return ToolResult.fail("缺少参数 instanceId");
        }

        Long instanceId = Long.valueOf(instanceIdObj.toString());

        // 当前先不真正轮询，先直接模拟查一次
        Map<String, Object> result = schedulerGatewayClient.queryInstanceStatus(instanceId);
        String status = String.valueOf(result.get("status"));

        if (!"SUCCESS".equals(status)) {
            return ToolResult.fail("任务实例未成功结束，当前状态: " + status);
        }

        return ToolResult.success("任务实例执行完成", result);
    }
}