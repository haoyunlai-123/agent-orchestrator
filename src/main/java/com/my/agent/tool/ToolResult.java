package com.my.agent.tool;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ToolResult {

    private boolean success;
    private String message;
    private Map<String, Object> output;

    public static ToolResult success(String message, Map<String, Object> output) {
        return ToolResult.builder()
                .success(true)
                .message(message)
                .output(output)
                .build();
    }

    public static ToolResult fail(String message) {
        return ToolResult.builder()
                .success(false)
                .message(message)
                .output(Map.of())
                .build();
    }
}