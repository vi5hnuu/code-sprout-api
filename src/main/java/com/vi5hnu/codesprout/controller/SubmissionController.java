package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.models.ApiResponse;
import com.vi5hnu.codesprout.models.RecordSubmissionRequest;
import com.vi5hnu.codesprout.services.UserSubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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
    public ResponseEntity<ApiResponse<Object>> record(
            @Valid @RequestBody RecordSubmissionRequest request,
            Principal principal) {
        var dto = submissionService.record(principal.getName(), request);
        return ResponseEntity.ok(new ApiResponse<>(true, dto));
    }

    /**
     * Returns the authenticated user's full submission history (paginated).
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> mySubmissions(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize,
            Principal principal) {
        var page = submissionService.getMySubmissions(principal.getName(), pageNo, pageSize);
        return ResponseEntity.ok(new ApiResponse<>(true, page));
    }

    /**
     * Returns the authenticated user's submissions for a specific problem (paginated).
     */
    @GetMapping("/my/problem/{problemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> mySubmissionsForProblem(
            @PathVariable String problemId,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize,
            Principal principal) {
        var page = submissionService.getMySubmissionsForProblem(principal.getName(), problemId, pageNo, pageSize);
        return ResponseEntity.ok(new ApiResponse<>(true, page));
    }

    /**
     * Returns the list of problem IDs that the authenticated user has accepted (solved).
     * Used by the frontend to mark solved problems in the problem list.
     */
    @GetMapping("/my/accepted-problems")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<String>>> myAcceptedProblems(Principal principal) {
        var ids = submissionService.getAcceptedProblemIds(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, ids));
    }
}
