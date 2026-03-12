package com.my.agent.tool.client.dto;

import lombok.Data;

@Data
public class TriggerOnceResponse {
    private Long instanceId;
    private boolean result;
}