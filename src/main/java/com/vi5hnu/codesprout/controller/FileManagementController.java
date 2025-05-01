package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.commons.Pageable;
import com.vi5hnu.codesprout.enums.ProblemDifficulty;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import com.vi5hnu.codesprout.models.CreateFileRequest;
import com.vi5hnu.codesprout.models.CreateFolderRequest;
import com.vi5hnu.codesprout.models.dto.*;
import com.vi5hnu.codesprout.services.FileManagementService;
import com.vi5hnu.codesprout.services.problemArchive.ProblemArchiveService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/v1/file-mgmt")
@RequiredArgsConstructor
public class FileManagementController {
    private final FileManagementService fileManagementService;

    @GetMapping(path = "folders")
    ResponseEntity<Map<String,Object>> getFolders(
            Principal principal,
            @RequestParam(name = "pageNo",required = false,defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize",required = false,defaultValue = "20") int pageSize) {
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFolders(principal.getName(),pageNo,pageSize)));
    }

    @GetMapping(path = "folders/{folderId}")
    ResponseEntity<Map<String,Object>> getFolderById(
            Principal principal,
            @PathVariable(name = "folderId") String folderId) {
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFolderById(principal.getName(),folderId)));
    }

    @GetMapping(path = "folders/{folderName}")
    ResponseEntity<Map<String,Object>> getFolderByName(
            Principal principal,
            @PathVariable(name = "folderName") String folderName) {
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFolderByName(principal.getName(),folderName)));
    }

    @GetMapping(path = "files/folder/{folderId}/")
    ResponseEntity<Map<String,Object>> getFiles(
            Principal principal,
            @PathVariable(name = "folderName") String folderId,
            @RequestParam(name = "pageNo",required = false,defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize",required = false,defaultValue = "20") int pageSize) throws Exception {
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFiles(principal.getName(),folderId,pageNo,pageSize)));
    }

    @GetMapping(path = "folder/{folderId}/file/{fileId}")
    ResponseEntity<Map<String,Object>> getFileById(
            Principal principal,
            @PathVariable(name = "folderId") String folderId,
            @PathVariable(name = "fileId") String fileId) throws Exception {
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFileById(principal.getName(),folderId,fileId)));
    }

    @GetMapping(path = "folder/{folderId}/file/{fileName}")
    ResponseEntity<Map<String,Object>> getFileByName(
            Principal principal,
            @PathVariable(name = "folderName") String folderId,
            @PathVariable(name = "fileName") String fileName) throws Exception {
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFileByName(principal.getName(),folderId,fileName)));
    }

    @GetMapping(path = "file/{fileId}")
    ResponseEntity<Map<String,Object>> getFileById(
            Principal principal,
            @PathVariable(name = "fileId") String fileId) {
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFileById(principal.getName(),fileId)));
    }

    @GetMapping(path = "file/{fileName}")
    ResponseEntity<Map<String,Object>> getFileByName(
            Principal principal,
            @PathVariable(name = "fileName") String fileName) {
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFileByName(principal.getName(),fileName)));
    }


//        public FolderDto createFolder(String ownerId, CreateFolderRequest createFolderRequest) throws Exception {
//        public FileDto createFile(String ownerId, CreateFileRequest createFileRequest,MultipartFile file) throws Exception {


    @PostMapping(path = "create-folder")
    ResponseEntity<Map<String,Object>> createFolder(Principal principal,@Valid  @RequestBody CreateFolderRequest folderRequest) throws Exception {
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.createFolder(principal.getName(),folderRequest)));
    }

    @PostMapping(path = "create-file",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    ResponseEntity<Map<String,Object>> createFile(Principal principal,@Valid  @RequestPart("info") CreateFileRequest fileRequest,@RequestPart("file") MultipartFile file) throws Exception {
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.createFile(principal.getName(),fileRequest,file)));
    }
}
