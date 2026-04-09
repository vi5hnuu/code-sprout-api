package com.vi5hnu.codesprout.controller;

import com.vi5hnu.codesprout.annotation.RequireUserWith;
import com.vi5hnu.codesprout.enums.FileAccess;
import com.vi5hnu.codesprout.enums.Visibility;
import com.vi5hnu.codesprout.exceptions.ApiException;
import com.vi5hnu.codesprout.models.CreateFileFromContentRequest;
import com.vi5hnu.codesprout.models.CreateFileRequest;
import com.vi5hnu.codesprout.models.CreateFolderRequest;
import com.vi5hnu.codesprout.models.UserRole;
import com.vi5hnu.codesprout.services.FileManagementService;
import com.vi5hnu.codesprout.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.List;
import java.util.Map;

//sourceUserId -> is not passed file will be searched for admin user else from sourceUserId
//only public files of sourceUserId are allowed to be shared
//sourceUserId must be valid user

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
            @RequestParam(name = "onlyFolders",required = false) Boolean onlyFolders,
            @RequestParam(name = "onlyFiles",required = false) Boolean onlyFiles,
            @RequestParam(name = "pageNo",required = false,defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize",required = false,defaultValue = "20") int pageSize) throws Exception {
        sourceUserId=validateAndGetSourceId(sourceUserId, principal.getName());
        if(onlyFolders==null) onlyFolders=false;
        if(onlyFiles==null) onlyFiles=false;
        final var ownerId=sourceUserId!=null ? sourceUserId:principal.getName();
        if((onlyFolders && onlyFiles) || (!onlyFolders && !onlyFiles)){
            return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getListing(ownerId,parentId,pageNo,pageSize,Visibility.PUBLIC,List.of(FileAccess.FREE,FileAccess.OPEN,FileAccess.PREMIUM))));
        }else if(onlyFiles){
            return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFiles(ownerId,parentId,pageNo,pageSize,null, Visibility.PUBLIC, List.of(FileAccess.FREE,FileAccess.OPEN,FileAccess.PREMIUM))));
        }else{
            return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFolders(ownerId,parentId,pageNo,pageSize)));
        }
    }

    @GetMapping(path = "folders")
    ResponseEntity<Map<String,Object>> getFolders(
            Principal principal,
            @RequestParam(name = "sourceUserId",required = false) String sourceUserId,
            @RequestParam(name = "parentId",required = false) String parentId,
            @RequestParam(name = "pageNo",required = false,defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize",required = false,defaultValue = "20") int pageSize) throws ApiException {
        final var ownerId=validateAndGetSourceId(sourceUserId,principal.getName());
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFolders(ownerId,parentId,pageNo,pageSize)));
    }

    @GetMapping(path = "folders/{folderId}")
    ResponseEntity<Map<String,Object>> getFolderById(
            Principal principal,
            @RequestParam(name = "sourceUserId",required = false) String sourceUserId,
            @PathVariable(name = "folderId") String folderId) throws ApiException {
        final var ownerId=validateAndGetSourceId(sourceUserId, principal.getName());
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFolderById(ownerId,folderId)));
    }

    @GetMapping(path = "folders/name/{folderName}")
    ResponseEntity<Map<String,Object>> getFolderByName(
            Principal principal,
            @RequestParam(name = "sourceUserId",required = false) String sourceUserId,
            @PathVariable(name = "folderName") String folderName) throws ApiException {
        final var ownerId=validateAndGetSourceId(sourceUserId, principal.getName());
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFolderByName(ownerId,folderName)));
    }

    @GetMapping(path = "files")
    ResponseEntity<Map<String,Object>> getFiles(
            Principal principal,
            @RequestParam(name = "sourceUserId",required = false) String sourceUserId,
            @RequestParam(name = "search",required = false) String search,
            @RequestParam(name = "folderId",required = false) String folderId,
            @RequestParam(name = "pageNo",required = false,defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize",required = false,defaultValue = "20") int pageSize) throws Exception {
        final var ownerId=validateAndGetSourceId(sourceUserId, principal.getName());
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFiles(ownerId,folderId,pageNo,pageSize,search, Visibility.PUBLIC, List.of(FileAccess.FREE,FileAccess.OPEN,FileAccess.PREMIUM))));
    }

    @GetMapping(path = "file-id/{fileId}")
    ResponseEntity<Map<String,Object>> getFileById(
            Principal principal,
            @RequestParam(name = "sourceUserId",required = false) String sourceUserId,
            @RequestParam(name = "folderId",required = false) String folderId,
            @PathVariable(name = "fileId") String fileId) throws Exception {
        final var ownerId=validateAndGetSourceId(sourceUserId, principal.getName());
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFileById(ownerId,folderId,fileId, Visibility.PUBLIC, List.of(FileAccess.FREE,FileAccess.OPEN,FileAccess.PREMIUM))));
    }

    @GetMapping(path = "file/{fileName}")
    ResponseEntity<Map<String,Object>> getFileByName(
            Principal principal,
            @RequestParam(name = "sourceUserId",required = false) String sourceUserId,
            @RequestParam(name = "folderId",required = false) String folderId,
            @PathVariable(name = "fileName") String fileName) throws Exception {
        final var ownerId=validateAndGetSourceId(sourceUserId, principal.getName());
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.getFileByName(ownerId,folderId,fileName,Visibility.PUBLIC,List.of(FileAccess.FREE,FileAccess.OPEN,FileAccess.PREMIUM))));
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

    @PostMapping(path = "delete-file/{fileId}")
    @RequireUserWith(hasRoles = {UserRole.ROLE_ADMIN})
    ResponseEntity<Map<String,Object>> deleteFile(Principal principal,@PathVariable("fileId") String fileId,@RequestParam("delete-permanently") Boolean permanentDelete ) throws Exception {
        this.fileManagementService.deleteFile(principal.getName(),fileId,permanentDelete);
        return ResponseEntity.status(200).body(Map.of("success",true,"message","file deleted successfully"));
    }

    @PostMapping(path = "delete-folder/{folderId}")
    @RequireUserWith(hasRoles = {UserRole.ROLE_ADMIN})
    ResponseEntity<Map<String,Object>> deleteFolder(Principal principal,@PathVariable("folderId") String folderId,@RequestParam("delete-permanently") Boolean permanentDelete ) throws Exception {
        this.fileManagementService.deleteFolder(principal.getName(),folderId,permanentDelete);
        return ResponseEntity.status(200).body(Map.of("success",true,"message","folder deleted successfully"));
    }

    @PostMapping(path = "content/create-file")
    @RequireUserWith(hasRoles = {UserRole.ROLE_ADMIN})
    ResponseEntity<Map<String,Object>> createFileFromContent(Principal principal,
                                                             @Valid  @RequestBody CreateFileFromContentRequest fileRequest) throws Exception {
        return ResponseEntity.status(200).body(Map.of("success",true,"data",this.fileManagementService.createFileFromContent(principal.getName(),fileRequest)));
    }

    private String validateAndGetSourceId(String sourceUserId,String currentUserId) throws ApiException {
        if(currentUserId==null) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid user id");
        if(sourceUserId!=null && !sourceUserId.equals(currentUserId)){
            final var user=userService.validateUser(sourceUserId);
        }
        if(sourceUserId==null){//no need to validate
            final var admin=userService.getAdmin().orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"user not found"));
            return admin.getId();
        }
        return sourceUserId;
    }
}
