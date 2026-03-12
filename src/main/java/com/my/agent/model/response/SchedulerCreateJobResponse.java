package com.my.agent.model.response;

import lombok.Data;

@Data
public class SchedulerCreateJobResponse {
    private Long id;
    private Long jobId;
    private String name;
}