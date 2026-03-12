package com.my.agent.model.response;

import lombok.Data;

@Data
public class SchedulerTriggerResponse {
    private Long instanceId;
    private Long jobId;
}