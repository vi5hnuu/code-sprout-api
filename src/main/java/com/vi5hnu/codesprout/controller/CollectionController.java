package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.exceptions.ApiException;
import com.vi5hnu.codesprout.services.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @GetMapping("/collections")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getMyCollections(Principal principal) {
        return ResponseEntity.ok(Map.of("success", true,
                "data", collectionService.getMyCollections(principal.getName())));
    }

    @GetMapping("/collections/{collectionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getDetail(
            @PathVariable String collectionId, Principal principal) throws ApiException {
        return ResponseEntity.ok(Map.of("success", true,
                "data", collectionService.getDetail(principal.getName(), collectionId)));
    }

    @PostMapping("/collections")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> create(
            @RequestBody Map<String, Object> body, Principal principal) {
        var dto = collectionService.create(
                principal.getName(),
                (String) body.get("name"),
                (String) body.get("description"),
                Boolean.TRUE.equals(body.get("is_public")));
        return ResponseEntity.ok(Map.of("success", true, "data", dto));
    }

    @PutMapping("/collections/{collectionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable String collectionId,
            @RequestBody Map<String, Object> body,
            Principal principal) throws ApiException {
        var dto = collectionService.update(
                principal.getName(), collectionId,
                (String) body.get("name"),
                (String) body.get("description"),
                Boolean.TRUE.equals(body.get("is_public")));
        return ResponseEntity.ok(Map.of("success", true, "data", dto));
    }

    @DeleteMapping("/collections/{collectionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> delete(
            @PathVariable String collectionId, Principal principal) throws ApiException {
        collectionService.delete(principal.getName(), collectionId);
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/collections/{collectionId}/problems/{problemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> toggleItem(
            @PathVariable String collectionId,
            @PathVariable String problemId,
            Principal principal) throws ApiException {
        boolean added = collectionService.toggleItem(principal.getName(), collectionId, problemId);
        return ResponseEntity.ok(Map.of("success", true, "data", Map.of("added", added)));
    }

    @GetMapping("/collections/problem/{problemId}/my-collections")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCollectionIdsContaining(
            @PathVariable String problemId, Principal principal) {
        return ResponseEntity.ok(Map.of("success", true,
                "data", collectionService.getCollectionIdsContaining(principal.getName(), problemId)));
    }
}
