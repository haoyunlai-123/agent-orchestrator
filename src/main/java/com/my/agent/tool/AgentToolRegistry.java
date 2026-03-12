package com.my.agent.tool;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AgentToolRegistry {

    private final List<AgentTool> tools;
    private final Map<String, AgentTool> toolMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (AgentTool tool : tools) {
            toolMap.put(tool.name(), tool);
        }
    }

    public AgentTool getTool(String name) {
        AgentTool tool = toolMap.get(name);
        if (tool == null) {
            throw new IllegalArgumentException("未找到对应工具: " + name);
        }
        return tool;
    }
}