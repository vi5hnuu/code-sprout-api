package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.enums.FileAccess;
import com.vi5hnu.codesprout.exceptions.ApiException;
import com.vi5hnu.codesprout.services.FileManagementService;
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


    @GetMapping(path = "file/{fileId}")
    ResponseEntity<Map<String,Object>> getFileById(@PathVariable(name = "fileId") String fileId) throws Exception {
        final var file=this.fileManagementService.getFileById(fileId);
        if(!file.getAccess().equals(FileAccess.OPEN)) throw new ApiException(HttpStatus.BAD_REQUEST,"you are not authorized to access this file");
        return ResponseEntity.status(200).body(Map.of("success",true,"data",file));
    }

    @GetMapping(path = "file-url/{fileId}")
    ResponseEntity<Map<String,Object>> getFileUrl(@PathVariable(name = "fileId") String fileId, Principal principal) throws Exception {
        final var file=this.fileManagementService.getFileById(fileId);
        final var fileUrl=String.format("%s/%s",s3BaseUrl,file.getS3Key());
        if(principal!=null){
            return ResponseEntity.status(200).body(Map.of("success",true,"data",fileUrl));
        }
        if(!file.getAccess().equals(FileAccess.OPEN)) throw new ApiException(HttpStatus.BAD_REQUEST,"you are not authorized to access this file");
        return ResponseEntity.status(200).body(Map.of("success",true,"data",fileUrl));
    }
}
