package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.enums.FileAccess;
import com.vi5hnu.codesprout.exceptions.ApiException;
import com.vi5hnu.codesprout.services.FileManagementService;
import com.vi5hnu.codesprout.services.FileViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/file-mgmt/open")
@RequiredArgsConstructor
public class OpenFileManagementController {
    @Value("${AWS_S3_BASE_URL}") private String s3BaseUrl;

    private final FileManagementService fileManagementService;
    private final FileViewService fileViewService;


    @GetMapping(path = "file/{fileId}")
    ResponseEntity<Map<String,Object>> getFileById(@PathVariable(name = "fileId") String fileId, Principal principal) throws Exception {
        final var file = this.fileManagementService.getFileById(fileId);
        checkAccess(file.getAccess(), principal);
        fileViewService.recordView(fileId, principal != null ? principal.getName() : null);
        return ResponseEntity.status(200).body(Map.of("success", true, "data", file));
    }

    @GetMapping(path = "file-url/{fileId}")
    ResponseEntity<Map<String,Object>> getFileUrl(@PathVariable(name = "fileId") String fileId, Principal principal) throws Exception {
        final var file = this.fileManagementService.getFileById(fileId);
        checkAccess(file.getAccess(), principal);
        final var fileUrl = String.format("%s/%s", s3BaseUrl, file.getS3Key());
        return ResponseEntity.status(200).body(Map.of("success", true, "data", fileUrl));
    }

    /**
     * Access rules:
     *   OPEN    — anyone (authenticated or not)
     *   FREE    — must be authenticated
     *   PREMIUM — must be authenticated (premium gating can be added here later)
     */
    private void checkAccess(FileAccess access, Principal principal) throws ApiException {
        if (access == FileAccess.OPEN) return;
        if (principal == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Please log in to access this content");
        }
        // PREMIUM tier check placeholder — enforce subscription when billing is added
    }
}
