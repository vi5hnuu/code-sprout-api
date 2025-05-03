package com.vi5hnu.codesprout.services;

import com.vi5hnu.codesprout.commons.Constants;
import com.vi5hnu.codesprout.commons.Pageable;
import com.vi5hnu.codesprout.entity.*;
import com.vi5hnu.codesprout.enums.FileExtension;
import com.vi5hnu.codesprout.models.*;
import com.vi5hnu.codesprout.models.dto.*;
import com.vi5hnu.codesprout.repository.*;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileManagementService {

    private static final Logger log = LoggerFactory.getLogger(FileManagementService.class);
    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    private final S3StorageService s3StorageService;
    private final UtilityService utilityService;

    @Transactional(readOnly = true)
    public Pageable<FolderDto> getFolders(String ownerId,String parentId,@Min(1) int pageNo, @Min(10) int limit) {
        PageRequest pageable = PageRequest.of(pageNo - 1, limit,Sort.by("name").ascending()); // Page index is 0-based in Spring Data
        final var folders=folderRepository.findAllByOwnerIdAndParentId(ownerId,parentId,pageable);
        return new Pageable<>(folders.get().map(this::folderToDto).toList(),pageNo,folders.getTotalPages());
    }

    @Transactional(readOnly = true)
    public FolderDto getFolderById(String ownerId,String folderId) {
        final var folder=folderRepository.findByOwnerIdAndId(ownerId,folderId).orElse(null);
        if(folder==null) return null;
        return folderToDto(folder);
    }

    @Transactional(readOnly = true)
    public FolderDto getFolderByName(String ownerId,String name) {
        final var folder=folderRepository.findByOwnerIdAndName(ownerId,name).orElse(null);
        if(folder==null) return null;
        return folderToDto(folder);
    }

    @Transactional(readOnly = true)
    public Pageable<FileDto> getFiles(String ownerId,String folderId,@Min(1) int pageNo, @Min(10) int limit) throws Exception {
        if(folderId!=null){
            final var folderExists=folderRepository.existsByOwnerIdAndId(ownerId, folderId);
            if(!folderExists) throw new Exception("Folder does not exists");
        }
        PageRequest pageable = PageRequest.of(pageNo - 1, limit,Sort.by("name").ascending()); // Page index is 0-based in Spring Data
        final var files=fileRepository.findAllByOwnerIdAndFolderId(ownerId,folderId,pageable);
        return new Pageable<>(files.get().map(this::fileToDto).toList(),pageNo,files.getTotalPages());
    }

    @Transactional(readOnly = true)
    public FileDto getFileById(String ownerId,String folderId,String fileId) throws Exception {
        if(folderId!=null){
            final var folderExists=folderRepository.existsByOwnerIdAndId(ownerId, folderId);
            if(!folderExists) throw new Exception("Folder does not exists");
        }
        final var file=fileRepository.findByOwnerIdAndFolderIdAndId(ownerId,folderId,fileId).orElse(null);
        if(file==null) return null;
        return fileToDto(file);
    }

    @Transactional(readOnly = true)
    public FileDto getFileByName(String ownerId,String folderId,String name) throws Exception {
        if(folderId!=null){
            final var folderExists=folderRepository.existsByOwnerIdAndId(ownerId, folderId);
            if(!folderExists) throw new Exception("Folder does not exists");
        }
        final var file=fileRepository.findByOwnerIdAndFolderIdAndName(ownerId,folderId,name).orElse(null);
        if(file==null) return null;
        return fileToDto(file);
    }

    @Transactional(readOnly = true)
    public FileDto getFileById(String ownerId,String fileId) {
        final var file=fileRepository.findByOwnerIdAndId(ownerId,fileId).orElse(null);
        if(file==null) return null;
        return fileToDto(file);
    }

    @Transactional(readOnly = true)
    public FileDto getFileByName(String ownerId,String name) {
        final var file=fileRepository.findByOwnerIdAndName(ownerId,name).orElse(null);
        if(file==null) return null;
        return fileToDto(file);
    }

    @Transactional(readOnly = false)
    public FolderDto createFolder(String ownerId, CreateFolderRequest createFolderRequest) throws Exception {
        final var folderExists=folderRepository.existsByOwnerIdAndParentIdAndName(ownerId,createFolderRequest.getParentId(),createFolderRequest.getName());
        if(folderExists) throw new Exception("folder with same name cannot be created.");

        final var newFolder=Folder.builder()
                .ownerId(ownerId)
                .name(createFolderRequest.getName())
                .description(createFolderRequest.getDescription())
                .parentId(createFolderRequest.getParentId())
                .password(createFolderRequest.getPassword())
                .build();
        final var savedFolder=folderRepository.saveAndFlush(newFolder);
        return folderToDto(savedFolder);
    }

    @Transactional(readOnly = false)
    public FileDto createFile(String ownerId, CreateFileRequest createFileRequest,MultipartFile multipartFile) throws Exception {
        final var originalFileName=multipartFile.getOriginalFilename();
        if(originalFileName==null) throw new Exception("Invalid file name");
        final var extension= FileExtension.fromValue(extractExtension(originalFileName));
        final var key=createFileRequest.getName()!=null ? createFileRequest.getName()+"."+extension.getValue() : multipartFile.getOriginalFilename();
        if(key==null) throw new Exception("Invalid file name");

        final var extensionMapping=Constants.allowedExtensions.get(extension.getValue());
        if(extensionMapping==null) throw new Exception("file type not supported");
        final var fileExists=fileRepository.existsByOwnerIdAndFolderIdAndName(ownerId,createFileRequest.getFolderId(),key);
        if(fileExists) throw new Exception("file with same name cannot be created.");

        final var file=utilityService.multipartToFile(multipartFile,key);
        final var mimeType=Files.probeContentType(file.toPath());
        if(!extensionMapping.equals(mimeType)) throw new Exception("File type not supported");

        try{
            final var uploadedFile=s3StorageService.uploadFile(file,key);
        }finally {
            if(file.delete()){
                log.info("Deleted temporary file");
            }else {
                log.warn("File deletion failed");
            }
        }

        final var newFile=File.builder()
                .ownerId(ownerId)
                .mimeType(mimeType)
                .s3Key(key)
                .fileSize(multipartFile.getSize())
                .folderId(createFileRequest.getFolderId())
                .fileExtension(extension)
                .name(key)
                .visibility(createFileRequest.getVisibility())
                .build();
        final var savedFile=fileRepository.save(newFile);
        return fileToDto(savedFile);
    }

    private String extractExtension(String fileName){
        String[] parts = fileName.split("\\.", 2);
        return parts.length == 2 ? parts[1] : "";
    }

    private FolderDto folderToDto(Folder folder){
        return FolderDto.builder()
                .id(folder.getId())
                .name(folder.getName())
                .description(folder.getDescription())
                .ownerId(folder.getOwnerId())
                .parentId(folder.getParentId())
                .password(folder.getPassword())
                .createdAt(folder.getCreatedAt())
                .updatedAt(folder.getUpdatedAt())
                .build();
    }

    private FileDto fileToDto(File file){
        return FileDto.builder()
                .id(file.getId())
                .name(file.getName())
                .ownerId(file.getOwnerId())
                .folderId(file.getFolderId())
                .fileExtension(file.getFileExtension())
                .fileSize(file.getFileSize())
                .s3Key(file.getS3Key())
                .mimeType(file.getMimeType())
                .visibility(file.getVisibility())
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .build();
    }
}