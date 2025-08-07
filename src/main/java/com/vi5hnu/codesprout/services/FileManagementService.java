package com.vi5hnu.codesprout.services;

import com.vi5hnu.codesprout.commons.Constants;
import com.vi5hnu.codesprout.commons.Pageable;
import com.vi5hnu.codesprout.entity.*;
import com.vi5hnu.codesprout.enums.FileExtension;
import com.vi5hnu.codesprout.enums.Visibility;
import com.vi5hnu.codesprout.exceptions.ApiException;
import com.vi5hnu.codesprout.models.*;
import com.vi5hnu.codesprout.models.folderStructure.FSItemDto;
import com.vi5hnu.codesprout.models.folderStructure.FileDto;
import com.vi5hnu.codesprout.models.folderStructure.FolderDto;
import com.vi5hnu.codesprout.repository.*;
import com.vi5hnu.codesprout.specifications.FileMgmtSpecification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * if folder is private -> cannot fetch folder/files even if they are public
 * if folder is public -> fetch files/folder that are public only
 * */

@Service
@RequiredArgsConstructor
public class FileManagementService {

    private static final Logger log = LoggerFactory.getLogger(FileManagementService.class);
    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    private final S3StorageService s3StorageService;
    private final UtilityService utilityService;

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Pageable<? extends FSItemDto> getListing(String ownerId, String parentId, @Min(1) int pageNo, @Min(10) int limit) throws ApiException {
        if(parentId!=null && !folderRepository.exists(FileMgmtSpecification.getFoldersBy(ownerId,parentId,null,Visibility.PUBLIC,false))) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid folder id");
        PageRequest folderPageable = PageRequest.of(pageNo - 1, limit,Sort.by(Sort.Direction.ASC, "name")); // Page index is 0-based in Spring Data
        final var foldersPage=folderRepository.findAll(FileMgmtSpecification.getFoldersBy(ownerId,null,null,parentId, Visibility.PUBLIC,false),folderPageable);
        final var totalFolders=foldersPage.getTotalElements();
        final var totalFiles=fileRepository.count(FileMgmtSpecification.getFilesBy(ownerId,null,null,parentId,Visibility.PUBLIC,false));
        final var foldersDto=foldersPage.stream().map(this::folderToDto).toList();
        if(foldersDto.size()<limit){//rest are filled by files
            final var totalFoldersPages=foldersPage.getTotalPages();
            final var filesPageNo=(pageNo-totalFoldersPages);
            final long skipCount=filesPageNo==0 ? 0 : ((long)totalFoldersPages*limit-totalFolders)+(long)(filesPageNo -1)*limit;
            final long limitCount=filesPageNo==0 ? limit-foldersDto.size() : limit;
            final var filesPage=this.findFilesBy(ownerId,parentId,skipCount,limitCount,Visibility.PUBLIC,Map.of("name",Sort.Direction.ASC));
            final var filesDtos=filesPage.stream().map(this::fileToDto).toList();

            List<FSItemDto> combined = new ArrayList<>(foldersDto);
            combined.addAll(filesDtos);
            return new Pageable<>(combined,pageNo,foldersPage.getTotalElements()+totalFiles);
        }
        return new Pageable<>(foldersDto,pageNo,foldersPage.getTotalElements()+totalFiles);
    }

    @Transactional(readOnly = true)
    public Pageable<FolderDto> getFolders(String ownerId,String parentId,@Min(1) int pageNo, @Min(10) int limit) {
        PageRequest pageable = PageRequest.of(pageNo - 1, limit,Sort.by(Sort.Direction.ASC,"name")); // Page index is 0-based in Spring Data
        final var folders=folderRepository.findAll(FileMgmtSpecification.getFoldersBy(ownerId,null,null,parentId,Visibility.PUBLIC,false),pageable);
        return new Pageable<>(folders.get().map(this::folderToDto).toList(),pageNo,folders.getTotalElements());
    }

    @Transactional(readOnly = true)
    public FolderDto getFolderById(String ownerId,String folderId) throws ApiException {
        if(folderId==null) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid folder id");
        final var folder=folderRepository.findOne(FileMgmtSpecification.getFoldersBy(ownerId,folderId,null,Visibility.PUBLIC,false)).orElse(null);
        if(folder==null) return null;
        return folderToDto(folder);
    }

    @Transactional(readOnly = true)
    public FolderDto getFolderByName(String ownerId,String name) throws ApiException {
        if(name==null) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid folder name");
        final var folder=folderRepository.findOne(FileMgmtSpecification.getFoldersBy(ownerId,null,name,Visibility.PUBLIC,false)).orElse(null);
        if(folder==null) return null;
        return folderToDto(folder);
    }

    @Transactional(readOnly = true)
    public Pageable<FileDto> getFiles(String ownerId, String folderId, @Min(1) int pageNo, @Min(10) int limit) throws Exception {
        if(folderId!=null){
            final var folderExists=folderRepository.exists(FileMgmtSpecification.getFoldersBy(ownerId,folderId,null,Visibility.PUBLIC,false));
            if(!folderExists) throw new Exception("Folder does not exists");
        }
        PageRequest pageable = PageRequest.of(pageNo - 1, limit,Sort.by(Sort.Direction.ASC,"name")); // Page index is 0-based in Spring Data
        final var files=fileRepository.findAll(FileMgmtSpecification.getFilesBy(ownerId,folderId,null,folderId,Visibility.PUBLIC,false),pageable);
        return new Pageable<>(files.get().map(this::fileToDto).toList(),pageNo,files.getTotalElements());
    }

    @Transactional(readOnly = true)
    public FileDto getFileById(String ownerId,String folderId,String fileId) throws Exception {
        if(fileId==null) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid file id");
        final var file=fileRepository.findOne(FileMgmtSpecification.getFilesBy(ownerId,fileId,null,folderId,Visibility.PUBLIC,false)).orElse(null);
        if(file==null) return null;


        if(folderId!=null){
            final var folderExists=folderRepository.exists(FileMgmtSpecification.getFoldersBy(ownerId,folderId,null,Visibility.PUBLIC,false));
            if(!folderExists) throw new Exception("Folder does not exists");
        }
        return fileToDto(file);
    }

    @Transactional(readOnly = true)
    public FileDto getFileByName(String ownerId,String folderId,String name) throws Exception {
        if(name==null) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid file name");
        final var file=fileRepository.findOne(FileMgmtSpecification.getFilesBy(ownerId,null,name,folderId,Visibility.PUBLIC,false)).orElse(null);
        if(file==null) return null;


        if(folderId!=null){
            final var folderExists=folderRepository.exists(FileMgmtSpecification.getFoldersBy(ownerId,folderId,null,Visibility.PUBLIC,false));
            if(!folderExists) throw new Exception("Folder does not exists");
        }
        return fileToDto(file);
    }

    @Transactional(readOnly = true)
    public FileDto getFileById(String ownerId,String fileId) throws ApiException {
        if(fileId==null) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid file id");
        final var file=fileRepository.findOne(FileMgmtSpecification.getFilesBy(ownerId,fileId,null,Visibility.PUBLIC,false)).orElse(null);
        if(file==null) return null;


        if(file.getFolderId()!=null){
            final var folderExists=folderRepository.exists(FileMgmtSpecification.getFoldersBy(ownerId,file.getFolderId(),null,Visibility.PUBLIC,false));
            if(!folderExists) throw new ApiException(HttpStatus.BAD_REQUEST,"Folder does not exists");
        }
        return fileToDto(file);
    }

    @Transactional(readOnly = true)
    public FileDto getFileByName(String ownerId,String name) throws ApiException {
        if(name==null) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid file name");
        final var file=fileRepository.findOne(FileMgmtSpecification.getFilesBy(ownerId,null,name,Visibility.PUBLIC,false)).orElse(null);
        if(file==null) return null;


        if(file.getFolderId()!=null){
            final var folderExists=folderRepository.exists(FileMgmtSpecification.getFoldersBy(ownerId,file.getFolderId(),null,Visibility.PUBLIC,false));
            if(!folderExists) throw new ApiException(HttpStatus.BAD_REQUEST,"Folder does not exists");
        }
        return fileToDto(file);
    }

    @Transactional(readOnly = false)
    public FolderDto createFolder(String ownerId, CreateFolderRequest createFolderRequest) throws Exception {
        final var folderExists=folderRepository.existsByOwnerIdAndParentIdAndName(ownerId,createFolderRequest.getParentId(),createFolderRequest.getName());
        if(folderExists) throw new ApiException(HttpStatus.BAD_REQUEST,"folder with same name cannot be created.");

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
        if(originalFileName==null) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid file name");
        final var extension= FileExtension.fromValue(extractExtension(originalFileName));
        final var key=createFileRequest.getName()!=null ? createFileRequest.getName()+"."+extension.getValue() : multipartFile.getOriginalFilename();
        if(key==null) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid file name");

        final var extensionMapping=Constants.allowedExtensions.get(extension.getValue());
        if(extensionMapping==null) throw new ApiException(HttpStatus.BAD_REQUEST,"file type not supported");
        final var fileExists=fileRepository.existsByOwnerIdAndFolderIdAndName(ownerId,createFileRequest.getFolderId(),key);
        if(fileExists) throw new ApiException(HttpStatus.BAD_REQUEST,"file with same name cannot be created.");

        final var file=utilityService.multipartToFile(multipartFile,key);
        final var mimeType=Files.probeContentType(file.toPath());
        if(!extensionMapping.equals(mimeType)) throw new ApiException(HttpStatus.BAD_REQUEST,"File type not supported");

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

    @Transactional(readOnly = false)
    public FileDto createFileFromContent(String ownerId, CreateFileFromContentRequest createFileFromContentRequest) throws Exception {
        final var fileName=createFileFromContentRequest.getFileName();
        if (fileName==null || !fileName.endsWith(".md")) {
            throw new ApiException(HttpStatus.BAD_REQUEST,"fileName must end with md");
        }

        if(createFileFromContentRequest.getContent()==null){
            throw new ApiException(HttpStatus.BAD_REQUEST,"file content cannot be empty");
        }

        final var fileExists=fileRepository.existsByOwnerIdAndFolderIdAndName(ownerId,createFileFromContentRequest.getFolderId(), fileName);
        if(fileExists) throw new ApiException(HttpStatus.BAD_REQUEST,"file with same name cannot be created.");

        final var file=utilityService.contentToFile(createFileFromContentRequest.getContent(), fileName);
        final var mimeType=Files.probeContentType(file.toPath());

        try{
            final var uploadedFile=s3StorageService.uploadFile(file,fileName);
            final var newFile=File.builder()
                    .ownerId(ownerId)
                    .mimeType(mimeType)
                    .s3Key(fileName)
                    .fileSize(file.length())
                    .folderId(createFileFromContentRequest.getFolderId())
                    .fileExtension(FileExtension.md)
                    .name(fileName)
                    .visibility(createFileFromContentRequest.getVisibility())
                    .build();
            final var savedFile=fileRepository.save(newFile);
            return fileToDto(savedFile);
        }finally {
            if(file.delete()){
                log.info("Deleted temporary file");
            }else {
                log.warn("File deletion failed");
            }
        }
    }

    private String extractExtension(String fileName){
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if(lastIndexOfDot==-1) return "";
        return fileName.substring(lastIndexOfDot+1);
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

    @Transactional(readOnly = true)
    public List<File> findFilesBy(String ownerId, String folderId, @Min(0) long offset, @Min(1) long limit, Visibility visibility, Map<String, Sort.Direction> sort) throws ApiException {
        List<String> allowedSortColumns = List.of("name");
        StringBuilder sql = new StringBuilder(String.format("SELECT * FROM %s WHERE owner_id = :ownerId",File.TABLE_NAME));

        if (folderId == null) {
            sql.append(" AND folder_id IS NULL");
        } else {
            sql.append(" AND folder_id = :folderId");
        }

        if(visibility!=null){
            sql.append(" AND visibility = :visibility");
        }

        if(!sort.isEmpty()){
            sql.append(" ORDER BY ");
            List<String> orderClauses = new ArrayList<>();
            for(final var columnSort : sort.entrySet()){
                if(!allowedSortColumns.contains(columnSort.getKey())) throw new ApiException(HttpStatus.BAD_REQUEST,"invalid column");
                orderClauses.add(String.format("%s %s",columnSort.getKey(),columnSort.getValue()));
            }
            sql.append(String.join(", ",orderClauses));
        }
        sql.append(" LIMIT :limit OFFSET :offset");

        Query query = entityManager.createNativeQuery(sql.toString(), File.class);
        query.setParameter("ownerId", ownerId);
        if (folderId != null) query.setParameter("folderId", folderId);
        if (visibility != null) query.setParameter("visibility", visibility.name());
        query.setParameter("offset", offset);
        query.setParameter("limit", limit);

        return query.getResultList();
    }
}