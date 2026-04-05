package com.vi5hnu.codesprout.services;

import com.vi5hnu.codesprout.configuration.JudgeConfig;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import com.vi5hnu.codesprout.models.RunCodeRequest;
import com.vi5hnu.codesprout.models.judge.JudgeExecuteRequest;
import com.vi5hnu.codesprout.models.judge.JudgeJobResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Log4j2
public class JudgeProxyService {

    private static final Map<ProblemLanguage, String> LANG_MAP = Map.of(
            ProblemLanguage.JAVASCRIPT, "javascript",
            ProblemLanguage.CPP,        "cpp",
            ProblemLanguage.SQL,        "sql"
    );

    private final JudgeConfig judgeConfig;
    private final WebClient   webClient;

    /**
     * Dispatches a code-run job to the judge.
     *
     * Non-blocking: returns a CompletableFuture so the servlet thread is freed
     * immediately — Tomcat's async dispatch picks up when the response arrives.
     *
     * Decorated with circuit breaker (opens after 50% failure on a 20-call window)
     * and retry (1 retry after 200ms) to survive transient judge hiccups.
     */
    @CircuitBreaker(name = "judge", fallbackMethod = "startJobFallback")
    @Retry(name = "judge")
    public CompletableFuture<String[]> startJob(RunCodeRequest req) {
        JudgeExecuteRequest body = buildExecuteRequest(req);

        return webClient.post()
                .uri(judgeConfig.getUrl() + "/execute")
                .header("X-API-Key", judgeConfig.getApiKey())
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        status -> status.is5xxServerError(),
                        resp -> resp.bodyToMono(String.class)
                                    .map(msg -> new RuntimeException("Judge returned 5xx: " + msg))
                )
                .bodyToMono(Map.class)
                .map(response -> {
                    String jobId       = (String) response.get("job_id");
                    String streamToken = (String) response.getOrDefault("stream_token", "");
                    if (streamToken.isBlank() && judgeConfig.getStreamSecret() != null) {
                        streamToken = generateStreamToken(jobId);
                    }
                    return new String[]{ jobId, streamToken };
                })
                .toFuture();
    }

    /**
     * Polls a completed job — REST fallback when WebSocket is unavailable.
     */
    @CircuitBreaker(name = "judge", fallbackMethod = "getJobFallback")
    public CompletableFuture<JudgeJobResponse> getJob(String jobId) {
        return webClient.get()
                .uri(judgeConfig.getUrl() + "/jobs/" + jobId)
                .header("X-API-Key", judgeConfig.getApiKey())
                .retrieve()
                .bodyToMono(JudgeJobResponse.class)
                .toFuture();
    }

    // ── Fallbacks — circuit open or repeated failure ──────────────────────────

    private CompletableFuture<String[]> startJobFallback(RunCodeRequest req, Throwable t) {
        log.error("Judge unavailable — startJob fallback triggered: {}", t.getMessage());
        return CompletableFuture.failedFuture(
                new RuntimeException("Judge service is temporarily unavailable. Please try again shortly.", t)
        );
    }

    private CompletableFuture<JudgeJobResponse> getJobFallback(String jobId, Throwable t) {
        log.error("Judge unavailable — getJob fallback triggered for job {}: {}", jobId, t.getMessage());
        return CompletableFuture.failedFuture(
                new RuntimeException("Judge service is temporarily unavailable.", t)
        );
    }

    // ── Stream token (HMAC-SHA256, identical to Go ls-judge/internal/api/stream_token.go) ──

    /**
     * Generates a 3-minute stream token scoped to {@code jobId}.
     * Format: {@code {job_id}:{expires_unix}:{hmac_hex}} — must stay in sync
     * with {@code GenerateStreamToken} in the Go judge.
     */
    public String generateStreamToken(String jobId) {
        String secret = judgeConfig.getStreamSecret();
        if (secret == null || secret.isBlank()) return "";
        long exp     = Instant.now().plusSeconds(180).getEpochSecond();
        String payload = jobId + ":" + exp;
        return payload + ":" + hmacSha256Hex(payload, secret);
    }

    private String hmacSha256Hex(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Stream token generation failed", e);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JudgeExecuteRequest buildExecuteRequest(RunCodeRequest req) {
        return JudgeExecuteRequest.builder()
                .language(LANG_MAP.getOrDefault(req.getLanguage(),
                        req.getLanguage().getValue().toLowerCase()))
                .code(req.getCode())
                .timeLimitMs(req.getTimeLimitMs() > 0 ? req.getTimeLimitMs() : 5_000)
                .memLimitMb(req.getMemLimitMb() > 0 ? req.getMemLimitMb() : 256)
                .testCases(buildTestCases(req))
                .build();
    }

    private List<JudgeExecuteRequest.JudgeTestCase> buildTestCases(RunCodeRequest req) {
        if (req.getCustomInput() != null && !req.getCustomInput().isBlank()) {
            return List.of(JudgeExecuteRequest.JudgeTestCase.builder()
                    .stdin(req.getCustomInput())
                    .expected("")
                    .build());
        }
        return req.getTestCases().stream()
                .map(tc -> JudgeExecuteRequest.JudgeTestCase.builder()
                        .stdin(tc.getInput())
                        .expected(tc.getExpectedOutput())
                        .build())
                .toList();
    }
}
