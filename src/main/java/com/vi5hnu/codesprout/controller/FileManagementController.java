package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.annotation.RequireUserWith;
import com.vi5hnu.codesprout.exceptions.ApiException;
import com.vi5hnu.codesprout.models.CreateFileFromContentRequest;
import com.vi5hnu.codesprout.models.CreateFileRequest;
import com.vi5hnu.codesprout.models.CreateFolderRequest;
import com.vi5hnu.codesprout.models.UserRole;
import com.vi5hnu.codesprout.services.FileManagementService;
import com.vi5hnu.codesprout.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.Map;

//sourceUserId -> is not passed file will be searched for currently logged in user else from sourceUserId
//only public files of sourceUserId are allowed to be shared
//sourceUserId must be valid user
//provided sourceUserId!=currently logged in userId

@RestController
@RequestMapping(path = "api/v1/file-mgmt")
@RequiredArgsConstructor
public class FileManagementController {
    private final FileManagementService fileManagementService;
    private final UserService userService;

    @GetMapping(path = "fs-listing")
    ResponseEntity<Map<String,Object>> getListing(
            Principal principal,
            @RequestParam(name = "sourceUserId",required = false) String sourceUserId,
            @RequestParam(name = "parentId",required = false) String parentId,
            @RequestParam(name = "pageNo",required = false,defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize",required = false,defaultValue = "20") int pageSize) throws ApiException {
        if(sourceUserId!=null && !sourceUserId.equals(principal.getName())){
            final var user=userService.validateUser(sourceUserId);
        }
        final var ownerId=sourceUserId!=null ? sourceUserId:principal.getName();
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getListing(ownerId,parentId,pageNo,pageSize)));
    }

    @GetMapping(path = "folders")
    ResponseEntity<Map<String,Object>> getFolders(
            Principal principal,
            @RequestParam(name = "sourceUserId",required = false) String sourceUserId,
            @RequestParam(name = "parentId",required = false) String parentId,
            @RequestParam(name = "pageNo",required = false,defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize",required = false,defaultValue = "20") int pageSize) throws ApiException {
        if(sourceUserId!=null && !sourceUserId.equals(principal.getName())){
            final var user=userService.validateUser(sourceUserId);
        }
        final var ownerId=sourceUserId!=null ? sourceUserId:principal.getName();
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFolders(ownerId,parentId,pageNo,pageSize)));
    }

    @GetMapping(path = "folders/{folderId}")
    ResponseEntity<Map<String,Object>> getFolderById(
            Principal principal,
            @RequestParam(name = "sourceUserId",required = false) String sourceUserId,
            @PathVariable(name = "folderId") String folderId) throws ApiException {
        if(sourceUserId!=null && !sourceUserId.equals(principal.getName())){
            final var user=userService.validateUser(sourceUserId);
        }
        final var ownerId=sourceUserId!=null ? sourceUserId:principal.getName();
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFolderById(ownerId,folderId)));
    }

    @GetMapping(path = "folders/{folderName}")
    ResponseEntity<Map<String,Object>> getFolderByName(
            Principal principal,
            @RequestParam(name = "sourceUserId",required = false) String sourceUserId,
            @PathVariable(name = "folderName") String folderName) throws ApiException {
        if(sourceUserId!=null && !sourceUserId.equals(principal.getName())){
            final var user=userService.validateUser(sourceUserId);
        }
        final var ownerId=sourceUserId!=null ? sourceUserId:principal.getName();
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFolderByName(ownerId,folderName)));
    }

    @GetMapping(path = "files")
    ResponseEntity<Map<String,Object>> getFiles(
            Principal principal,
            @RequestParam(name = "sourceUserId",required = false) String sourceUserId,
            @RequestParam(name = "folderId",required = false) String folderId,
            @RequestParam(name = "pageNo",required = false,defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize",required = false,defaultValue = "20") int pageSize) throws Exception {
        if(sourceUserId!=null && !sourceUserId.equals(principal.getName())){
            final var user=userService.validateUser(sourceUserId);
        }
        final var ownerId=sourceUserId!=null ? sourceUserId:principal.getName();
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFiles(ownerId,folderId,pageNo,pageSize)));
    }

    @GetMapping(path = "file/{fileId}")
    ResponseEntity<Map<String,Object>> getFileById(
            Principal principal,
            @RequestParam(name = "sourceUserId",required = false) String sourceUserId,
            @RequestParam(name = "folderId",required = false) String folderId,
            @PathVariable(name = "fileId") String fileId) throws Exception {
        if(sourceUserId!=null && !sourceUserId.equals(principal.getName())){
            final var user=userService.validateUser(sourceUserId);
        }
        final var ownerId=sourceUserId!=null ? sourceUserId:principal.getName();
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFileById(ownerId,folderId,fileId)));
    }

    @GetMapping(path = "file/{fileName}")
    ResponseEntity<Map<String,Object>> getFileByName(
            Principal principal,
            @RequestParam(name = "sourceUserId",required = false) String sourceUserId,
            @RequestParam(name = "folderId",required = false) String folderId,
            @PathVariable(name = "fileName") String fileName) throws Exception {
        if(sourceUserId!=null && !sourceUserId.equals(principal.getName())){
            final var user=userService.validateUser(sourceUserId);
        }
        final var ownerId=sourceUserId!=null ? sourceUserId:principal.getName();
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFileByName(ownerId,folderId,fileName)));
    }

    @PostMapping(path = "create-folder")
    @RequireUserWith(hasRoles = {UserRole.ROLE_ADMIN})
    ResponseEntity<Map<String,Object>> createFolder(Principal principal,
                                                    @Valid  @RequestBody CreateFolderRequest folderRequest) throws Exception {
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.createFolder(principal.getName(),folderRequest)));
    }

    @PostMapping(path = "create-file",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @RequireUserWith(hasRoles = {UserRole.ROLE_ADMIN})
    ResponseEntity<Map<String,Object>> createFile(Principal principal,
                                                  @Valid  @RequestPart("info") CreateFileRequest fileRequest,@RequestPart("file") MultipartFile file) throws Exception {
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.createFile(principal.getName(),fileRequest,file)));
    }

    @PostMapping(path = "content/create-file",consumes = {MediaType.APPLICATION_JSON_VALUE})
    @RequireUserWith(hasRoles = {UserRole.ROLE_ADMIN})
    ResponseEntity<Map<String,Object>> createFileFromContent(Principal principal,
                                                             @Valid  @RequestPart("info") CreateFileFromContentRequest fileRequest) throws Exception {
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.createFileFromContent(principal.getName(),fileRequest)));
    }
}
