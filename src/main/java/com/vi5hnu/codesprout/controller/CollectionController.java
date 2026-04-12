package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.exceptions.ApiException;
import com.vi5hnu.codesprout.models.ApiResponse;
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
    public ResponseEntity<ApiResponse<Object>> getMyCollections(Principal principal) {
        return ResponseEntity.ok(new ApiResponse<>(true, collectionService.getMyCollections(principal.getName())));
    }

    @GetMapping("/collections/{collectionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> getDetail(
            @PathVariable String collectionId, Principal principal) throws ApiException {
        return ResponseEntity.ok(new ApiResponse<>(true, collectionService.getDetail(principal.getName(), collectionId)));
    }

    @PostMapping("/collections")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> create(
            @RequestBody Map<String, Object> body, Principal principal) {
        var dto = collectionService.create(
                principal.getName(),
                (String) body.get("name"),
                (String) body.get("description"),
                Boolean.TRUE.equals(body.get("is_public")));
        return ResponseEntity.ok(new ApiResponse<>(true, dto));
    }

    @PutMapping("/collections/{collectionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> update(
            @PathVariable String collectionId,
            @RequestBody Map<String, Object> body,
            Principal principal) throws ApiException {
        var dto = collectionService.update(
                principal.getName(), collectionId,
                (String) body.get("name"),
                (String) body.get("description"),
                Boolean.TRUE.equals(body.get("is_public")));
        return ResponseEntity.ok(new ApiResponse<>(true, dto));
    }

    @DeleteMapping("/collections/{collectionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String collectionId, Principal principal) throws ApiException {
        collectionService.delete(principal.getName(), collectionId);
        return ResponseEntity.ok(new ApiResponse<>(true, null));
    }

    @PostMapping("/collections/{collectionId}/problems/{problemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> toggleItem(
            @PathVariable String collectionId,
            @PathVariable String problemId,
            Principal principal) throws ApiException {
        boolean added = collectionService.toggleItem(principal.getName(), collectionId, problemId);
        return ResponseEntity.ok(new ApiResponse<>(true, Map.of("added", added)));
    }

    @GetMapping("/collections/problem/{problemId}/my-collections")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> getCollectionIdsContaining(
            @PathVariable String problemId, Principal principal) {
        return ResponseEntity.ok(new ApiResponse<>(true, collectionService.getCollectionIdsContaining(principal.getName(), problemId)));
    }
}
