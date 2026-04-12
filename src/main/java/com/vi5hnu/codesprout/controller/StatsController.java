package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.models.ApiResponse;
import com.vi5hnu.codesprout.services.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/users/stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Object>> getMyStats(Principal principal) {
        return ResponseEntity.ok(new ApiResponse<>(true, statsService.getStats(principal.getName())));
    }
}
