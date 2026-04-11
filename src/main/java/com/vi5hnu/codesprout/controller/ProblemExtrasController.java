package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.enums.ProblemDifficulty;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
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
import java.util.HashMap;
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
    public ResponseEntity<Map<String, Object>> getHints(@PathVariable String problemId) {
        return ResponseEntity.ok(Map.of("success", true, "data", hintService.getHints(problemId)));
    }

    @PostMapping("/admin/problem/{problemId}/hints")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> addHint(
            @PathVariable String problemId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(Map.of("success", true,
                "data", hintService.addHint(problemId, body.get("content"))));
    }

    @PutMapping("/admin/hints/{hintId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateHint(
            @PathVariable String hintId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(Map.of("success", true,
                "data", hintService.updateHint(hintId, body.get("content"))));
    }

    @DeleteMapping("/admin/hints/{hintId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteHint(@PathVariable String hintId) {
        hintService.deleteHint(hintId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ── Templates ────────────────────────────────────────────────────────────

    @GetMapping("/problem/{problemId}/templates")
    public ResponseEntity<Map<String, Object>> getTemplates(@PathVariable String problemId) {
        return ResponseEntity.ok(Map.of("success", true, "data", templateService.getTemplates(problemId)));
    }

    @GetMapping("/problem/{problemId}/templates/{language}")
    public ResponseEntity<Map<String, Object>> getTemplate(
            @PathVariable String problemId,
            @PathVariable ProblemLanguage language) {
        return ResponseEntity.ok(Map.of("success", true,
                "data", templateService.getTemplate(problemId, language).orElse(null)));
    }

    @PutMapping("/admin/problem/{problemId}/templates/{language}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> upsertTemplate(
            @PathVariable String problemId,
            @PathVariable ProblemLanguage language,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(Map.of("success", true,
                "data", templateService.upsert(problemId, language, body.get("template_code"))));
    }

    @DeleteMapping("/admin/problem/{problemId}/templates/{language}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteTemplate(
            @PathVariable String problemId,
            @PathVariable ProblemLanguage language) {
        templateService.delete(problemId, language);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ── Problem of the Day ───────────────────────────────────────────────────

    @GetMapping("/problem/today")
    public ResponseEntity<Map<String, Object>> getProblemOfDay() {
        var pod = podService.getToday().orElse(null);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", pod); // null allowed

        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/problem/today")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> scheduleProblemOfDay(
            @RequestBody Map<String, String> body) {
        LocalDate date = body.containsKey("date")
                ? LocalDate.parse(body.get("date"))
                : LocalDate.now();
        return ResponseEntity.ok(Map.of("success", true,
                "data", podService.schedule(body.get("problem_id"), date)));
    }

    // ── Related Problems ─────────────────────────────────────────────────────

    @GetMapping("/problem/{slug}/related")
    public ResponseEntity<Map<String, Object>> getRelated(
            @PathVariable String slug,
            @RequestParam(defaultValue = "5") int limit) {
        try {
            return ResponseEntity.ok(Map.of("success", true,
                    "data", problemArchiveService.getRelatedProblems(slug, limit)));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", true, "data", java.util.List.of()));
        }
    }
}
