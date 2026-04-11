package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.enums.BookmarkType;
import com.vi5hnu.codesprout.services.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /**
     * Generic bookmark toggle.
     * Type is the BookmarkType enum value (e.g. PROBLEM, ARTICLE).
     * targetId is the ID of the resource being bookmarked.
     *
     * POST /api/v1/bookmark/PROBLEM/{problemId}
     * POST /api/v1/bookmark/ARTICLE/{articleId}
     */
    @PostMapping("/bookmark/{type}/{targetId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> toggle(
            @PathVariable BookmarkType type,
            @PathVariable String targetId,
            Principal principal) {
        boolean bookmarked = bookmarkService.toggle(principal.getName(), type, targetId);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", Map.of("bookmarked", bookmarked)
        ));
    }

    /**
     * Returns all bookmarked problems for the authenticated user.
     * GET /api/v1/my/bookmarks/problems
     */
    @GetMapping("/my/bookmarks/problems")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> myProblemBookmarks(Principal principal) {
        var bookmarks = bookmarkService.getMyProblemBookmarks(principal.getName());
        return ResponseEntity.ok(Map.of("success", true, "data", bookmarks));
    }

    /**
     * Placeholder for future article bookmarks.
     * GET /api/v1/my/bookmarks/articles
     */
    @GetMapping("/my/bookmarks/articles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> myArticleBookmarks(Principal principal) {
        // TODO: implement when article bookmark fetch is needed
        return ResponseEntity.ok(Map.of("success", true, "data", java.util.List.of()));
    }
}
