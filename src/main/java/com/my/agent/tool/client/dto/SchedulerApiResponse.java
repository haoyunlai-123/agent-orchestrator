package com.my.agent.tool.client.dto;

import lombok.Data;

@Data
public class SchedulerApiResponse<T> {
    private Integer code;
    private String msg;
    private T data;
}