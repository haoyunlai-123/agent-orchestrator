package com.my.agent.controller;

import com.my.agent.model.request.AgentRunRequest;
import com.my.agent.model.response.AgentRunResponse;
import com.my.agent.model.response.ApiResponse;
import com.my.agent.service.AgentRunService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentRunController {

    private final AgentRunService agentRunService;

    @PostMapping("/run")
    public ApiResponse<AgentRunResponse> run(@Valid @RequestBody AgentRunRequest request) {
        return ApiResponse.success(agentRunService.run(request));
    }
}