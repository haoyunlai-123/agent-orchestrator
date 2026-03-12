package com.my.agent.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateJobCommand {
    private String name;
    private String scheduleType;
    private String scheduleExpr;
    private String handlerType;
    private String handlerParam;
    private String routeStrategy;
    private Integer retryMax;
    private Integer timeoutMs;
    private Boolean enabled;
}