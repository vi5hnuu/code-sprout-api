package com.vi5hnu.codesprout.exceptions.handler;

import com.vi5hnu.codesprout.enums.Profile;
import com.vi5hnu.codesprout.exceptions.ApiException;
import com.vi5hnu.codesprout.exceptions.UserAlreadyExistsException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    // ── Business exceptions ───────────────────────────────────────────────────

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> userAlreadyExists(
            UserAlreadyExistsException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("success", false, "message", ex.getMessage()));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> apiException(ApiException ex, WebRequest request) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(Map.of("success", false, "message", ex.getMessage()));
    }

    // ── Security exceptions ───────────────────────────────────────────────────

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("success", false, "message", "Access denied"));
    }

    // ── Rate limit & HTTP status exceptions ───────────────────────────────────

    /**
     * Handles ResponseStatusException thrown by RateLimitAspect (429) and any
     * other place that uses ResponseStatusException directly.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(
            ResponseStatusException ex, WebRequest request) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("success", false, "message", ex.getReason() != null ? ex.getReason() : ex.getMessage()));
    }

    // ── Async / CompletableFuture exceptions ──────────────────────────────────

    /**
     * Unwraps CompletableFuture/CompletionStage exceptions so the real cause
     * reaches the appropriate handler (e.g. judge circuit-breaker fallback).
     */
    @ExceptionHandler({CompletionException.class, ExecutionException.class})
    public ResponseEntity<Map<String, Object>> handleAsyncException(
            Exception ex, WebRequest request) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        if (cause instanceof ResponseStatusException rse) {
            return handleResponseStatusException(rse, request);
        }
        if (cause instanceof ApiException ae) {
            return apiException(ae, request);
        }
        log.error("Async execution failure", cause);
        String message = isProd() ? "An unexpected error occurred" : cause.getMessage();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("success", false, "message", message));
    }

    /**
     * Fired when the judge takes longer than the MVC async timeout (configured
     * to 40 s in application.properties). Return 503 so the client can retry.
     *
     * Must be an @Override, NOT a new @ExceptionHandler — ResponseEntityExceptionHandler
     * already handles AsyncRequestTimeoutException via handleException(), so a
     * second @ExceptionHandler for the same type causes an ambiguous mapping error.
     */
    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(
            AsyncRequestTimeoutException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        log.warn("Async request timed out");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("success", false, "message", "Request timed out. Please try again."));
    }

    // ── Validation exceptions ─────────────────────────────────────────────────

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        StringBuilder errorBuilder = new StringBuilder();
        final var errors = ex.getBindingResult().getFieldErrors();
        for (int i = 0; i < errors.size(); i++) {
            errorBuilder.append(errors.get(i).getDefaultMessage());
            if (i < errors.size() - 1) errorBuilder.append(", ");
        }
        return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "Validation failed", "error", errorBuilder.toString()));
    }

    // ── Catch-all fallback ────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex, WebRequest request) {
        log.error("Unhandled exception", ex);
        String message = isProd() ? "An unexpected error occurred" : ex.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", message));
    }

    private boolean isProd() {
        return Profile.PROD.profile.equals(activeProfile);
    }
}
