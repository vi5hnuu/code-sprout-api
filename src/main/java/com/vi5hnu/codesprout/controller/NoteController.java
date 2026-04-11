package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.models.ApiResponse;
import com.vi5hnu.codesprout.models.UserNoteDto;
import com.vi5hnu.codesprout.services.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping("/notes/problem/{problemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserNoteDto>> getNote(
            @PathVariable String problemId, Principal principal) {
        var note = noteService.getNote(principal.getName(), problemId).orElse(null);

        return ResponseEntity.ok(new ApiResponse<>(true,note));
    }

    @PutMapping("/notes/problem/{problemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserNoteDto>> upsertNote(
            @PathVariable String problemId,
            @RequestBody Map<String, String> body,
            Principal principal) {
        var note = noteService.upsert(principal.getName(), problemId, body.getOrDefault("content", ""));
        return ResponseEntity.ok(new ApiResponse<>( true, note));
    }

    @DeleteMapping("/notes/problem/{problemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> deleteNote(
            @PathVariable String problemId, Principal principal) {
        noteService.delete(principal.getName(), problemId);
        return ResponseEntity.ok(new ApiResponse<>(true,null));
    }
}
