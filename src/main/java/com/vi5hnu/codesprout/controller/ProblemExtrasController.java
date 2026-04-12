package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.enums.ProblemDifficulty;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import com.vi5hnu.codesprout.models.ApiResponse;
import com.vi5hnu.codesprout.services.ProblemHintService;
import com.vi5hnu.codesprout.services.ProblemOfDayService;
import com.vi5hnu.codesprout.services.ProblemTemplateService;
import com.vi5hnu.codesprout.services.problemArchive.ProblemArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class ProblemExtrasController {

    private final ProblemHintService hintService;
    private final ProblemTemplateService templateService;
    private final ProblemOfDayService podService;
    private final ProblemArchiveService problemArchiveService;

    // ── Hints ────────────────────────────────────────────────────────────────

    @GetMapping("/problem/{problemId}/hints")
    public ResponseEntity<ApiResponse<Object>> getHints(@PathVariable String problemId) {
        return ResponseEntity.ok(new ApiResponse<>(true, hintService.getHints(problemId)));
    }

    @PostMapping("/admin/problem/{problemId}/hints")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> addHint(
            @PathVariable String problemId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(new ApiResponse<>(true, hintService.addHint(problemId, body.get("content"))));
    }

    @PutMapping("/admin/hints/{hintId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> updateHint(
            @PathVariable String hintId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(new ApiResponse<>(true, hintService.updateHint(hintId, body.get("content"))));
    }

    @DeleteMapping("/admin/hints/{hintId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteHint(@PathVariable String hintId) {
        hintService.deleteHint(hintId);
        return ResponseEntity.ok(new ApiResponse<>(true, null));
    }

    // ── Templates ────────────────────────────────────────────────────────────

    @GetMapping("/problem/{problemId}/templates")
    public ResponseEntity<ApiResponse<Object>> getTemplates(@PathVariable String problemId) {
        return ResponseEntity.ok(new ApiResponse<>(true, templateService.getTemplates(problemId)));
    }

    @GetMapping("/problem/{problemId}/templates/{language}")
    public ResponseEntity<ApiResponse<Object>> getTemplate(
            @PathVariable String problemId,
            @PathVariable ProblemLanguage language) {
        return ResponseEntity.ok(new ApiResponse<>(true, templateService.getTemplate(problemId, language).orElse(null)));
    }

    @PutMapping("/admin/problem/{problemId}/templates/{language}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> upsertTemplate(
            @PathVariable String problemId,
            @PathVariable ProblemLanguage language,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(new ApiResponse<>(true, templateService.upsert(problemId, language, body.get("template_code"))));
    }

    @DeleteMapping("/admin/problem/{problemId}/templates/{language}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(
            @PathVariable String problemId,
            @PathVariable ProblemLanguage language) {
        templateService.delete(problemId, language);
        return ResponseEntity.ok(new ApiResponse<>(true, null));
    }

    // ── Problem of the Day ───────────────────────────────────────────────────

    @GetMapping("/problem/today")
    public ResponseEntity<ApiResponse<Object>> getProblemOfDay() {
        var pod = podService.getToday().orElse(null);
        return ResponseEntity.ok(new ApiResponse<>(pod!=null, pod));
    }

    @PutMapping("/admin/problem/today")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> scheduleProblemOfDay(
            @RequestBody Map<String, String> body) {
        LocalDate date = body.containsKey("date")
                ? LocalDate.parse(body.get("date"))
                : LocalDate.now();
        return ResponseEntity.ok(new ApiResponse<>(true, podService.schedule(body.get("problem_id"), date)));
    }

    // ── Related Problems ─────────────────────────────────────────────────────

    @GetMapping("/problem/{slug}/related")
    public ResponseEntity<ApiResponse<Object>> getRelated(
            @PathVariable String slug,
            @RequestParam(defaultValue = "5") int limit) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(true, problemArchiveService.getRelatedProblems(slug, limit)));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(true, List.of()));
        }
    }
}
