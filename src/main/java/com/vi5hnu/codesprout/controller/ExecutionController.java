package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.annotation.RateLimit;
import com.vi5hnu.codesprout.configuration.JudgeConfig;
import com.vi5hnu.codesprout.models.RunCodeRequest;
import com.vi5hnu.codesprout.models.judge.JudgeJobResponse;
import com.vi5hnu.codesprout.services.JudgeProxyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/v1/execution")
@RequiredArgsConstructor
public class ExecutionController {

    private final JudgeProxyService judgeProxyService;
    private final JudgeConfig       judgeConfig;

    /**
     * Start a code execution job.
     *
     * Returns {@code job_id} and a short-lived {@code stream_token} that the
     * frontend uses to open a WebSocket <b>directly</b> to the judge:
     * <pre>
     *   wss://judge-host/jobs/{job_id}?stream_token={stream_token}
     * </pre>
     *
     * The servlet thread is released immediately — Spring MVC's async dispatch
     * resumes when the judge responds. No thread is pinned during the round-trip.
     */
    /** 20 executions per minute per user. Judge is expensive — protect it. */
    @RateLimit(capacity = 20, time = 1, unit = TimeUnit.MINUTES)
    @PostMapping("/run")
    @PreAuthorize("isAuthenticated()")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> run(
            @Valid @RequestBody RunCodeRequest request) {

        return judgeProxyService.startJob(request)
                .thenApply(result -> {
                    String jobId       = result[0];
                    String streamToken = result[1];
                    String wsBase = judgeConfig.getUrl().replaceFirst("^http", "ws");
                    String wsUrl  = wsBase + "/jobs/" + jobId + "?stream_token=" + streamToken;

                    return ResponseEntity.accepted().<Map<String, Object>>body(Map.of(
                            "success", true,
                            "data", Map.of(
                                    "job_id",       jobId,
                                    "stream_token", streamToken,
                                    "ws_url",       wsUrl
                            )
                    ));
                });
    }

    /**
     * Poll job status — REST fallback when WebSocket is unavailable.
     */
    @GetMapping("/jobs/{jobId}")
    @PreAuthorize("isAuthenticated()")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getJob(
            @PathVariable String jobId) {

        return judgeProxyService.getJob(jobId)
                .thenApply(job ->
                        ResponseEntity.ok().<Map<String, Object>>body(Map.of("success", true, "data", job))
                );
    }
}
