package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.models.RecordSubmissionRequest;
import com.vi5hnu.codesprout.services.UserSubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("api/v1/submission")
@RequiredArgsConstructor
public class SubmissionController {

    private final UserSubmissionService submissionService;

    /**
     * Called by the frontend after the judge returns a result, to persist it server-side.
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> record(
            @Valid @RequestBody RecordSubmissionRequest request,
            Principal principal) {
        var dto = submissionService.record(principal.getName(), request);
        return ResponseEntity.ok(Map.of("success", true, "data", dto));
    }

    /**
     * Returns the authenticated user's full submission history (paginated).
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> mySubmissions(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize,
            Principal principal) {
        var page = submissionService.getMySubmissions(principal.getName(), pageNo, pageSize);
        return ResponseEntity.ok(Map.of("success", true, "data", page));
    }

    /**
     * Returns the authenticated user's submissions for a specific problem (paginated).
     */
    @GetMapping("/my/problem/{problemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> mySubmissionsForProblem(
            @PathVariable String problemId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize,
            Principal principal) {
        var page = submissionService.getMySubmissionsForProblem(principal.getName(), problemId, pageNo, pageSize);
        return ResponseEntity.ok(Map.of("success", true, "data", page));
    }

    /**
     * Returns the list of problem IDs that the authenticated user has accepted (solved).
     * Used by the frontend to mark solved problems in the problem list.
     */
    @GetMapping("/my/accepted-problems")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> myAcceptedProblems(Principal principal) {
        var ids = submissionService.getAcceptedProblemIds(principal.getName());
        return ResponseEntity.ok(Map.of("success", true, "data", ids));
    }
}
