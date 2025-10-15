package com.vi5hnu.codesprout.services;

import com.vi5hnu.codesprout.commons.Constants;
import com.vi5hnu.codesprout.commons.Pageable;
import com.vi5hnu.codesprout.entity.*;
import com.vi5hnu.codesprout.enums.FileAccess;
import com.vi5hnu.codesprout.enums.FileExtension;
import com.vi5hnu.codesprout.enums.Visibility;
import com.vi5hnu.codesprout.exceptions.ApiException;
import com.vi5hnu.codesprout.models.*;
import com.vi5hnu.codesprout.models.folderStructure.FSItemDto;
import com.vi5hnu.codesprout.models.folderStructure.ExtFile;
import com.vi5hnu.codesprout.models.folderStructure.FolderDto;
import com.vi5hnu.codesprout.repository.*;
import com.vi5hnu.codesprout.specifications.FileMgmtSpecification;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
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
import java.util.stream.Collectors;

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
    public Pageable<? extends FSItemDto> getListing(String ownerId, String parentId, @Min(1) int pageNo, @Min(10) int limit,Visibility visibility,List<FileAccess> access) throws ApiException {
        if(parentId!=null && !folderRepository.exists(FileMgmtSpecification.getFoldersBy(ownerId,parentId,null,visibility,false))) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid folder id");
        PageRequest folderPageable = PageRequest.of(pageNo - 1, limit,Sort.by(Sort.Direction.ASC, "name")); // Page index is 0-based in Spring Data
        final var foldersPage=folderRepository.findAll(FileMgmtSpecification.getFoldersBy(ownerId,null,null,parentId, visibility,false),folderPageable);
        final var totalFolders=foldersPage.getTotalElements();
        final var totalFiles=fileRepository.count(FileMgmtSpecification.getFilesBy(ownerId,null,null,null,parentId,visibility,access,false));
        final var foldersDto=foldersPage.stream().map(this::folderToDto).toList();
        if(foldersDto.size()<limit){//rest are filled by files
            final var totalFoldersPages=foldersPage.getTotalPages();
            final var filesPageNo=(pageNo-totalFoldersPages);
            final long skipCount=filesPageNo==0 ? 0 : ((long)totalFoldersPages*limit-totalFolders)+(long)(filesPageNo -1)*limit;
            final long limitCount=filesPageNo==0 ? limit-foldersDto.size() : limit;
            final var filesPage=this.findFilesBy(ownerId,parentId,skipCount,limitCount,visibility,access,Map.of("name",Sort.Direction.ASC),false);
            final var filesDtos=filesPage.stream().map(ExtFile::fromFile).toList();

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
    public Pageable<ExtFile> getFiles(String ownerId, String folderId, @Min(1) int pageNo, @Min(10) int limit,String search, Visibility visibility, List<FileAccess> access) throws Exception {
        if(visibility==null) visibility=Visibility.PUBLIC;
        if(access==null) access=List.of();

        if(folderId!=null){
            final var folderExists=folderRepository.exists(FileMgmtSpecification.getFoldersBy(ownerId,folderId,null,visibility,false));
            if(!folderExists) throw new Exception("Folder does not exists");
        }
        PageRequest pageable = PageRequest.of(pageNo - 1, limit,Sort.by(Sort.Direction.ASC,"name")); // Page index is 0-based in Spring Data
        final var files=fileRepository.findAll(FileMgmtSpecification.getFilesBy(ownerId,folderId,null,search,folderId,visibility,access,false),pageable);
        return new Pageable<>(files.get().map(ExtFile::fromFile).toList(),pageNo,files.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ExtFile getFileById(String ownerId, String folderId, String fileId, Visibility visibility, List<FileAccess> access) throws Exception {
        if(fileId==null) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid file id");
        final var file=fileRepository.findOne(FileMgmtSpecification.getFilesBy(ownerId,fileId,null,null,folderId,visibility,access,false)).orElse(null);
        if(file==null) return null;


        if(folderId!=null){
            final var folderExists=folderRepository.exists(FileMgmtSpecification.getFoldersBy(ownerId,folderId,null,Visibility.PUBLIC,false));
            if(!folderExists) throw new Exception("Folder does not exists");
        }
        return ExtFile.fromFile(file);
    }

    @Transactional(readOnly = true)
    public ExtFile getFileByName(String ownerId, String folderId, String name, Visibility visibility, List<FileAccess> access) throws Exception {
        if(name==null) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid file name");
        final var file=fileRepository.findOne(FileMgmtSpecification.getFilesBy(ownerId,null,name,null,folderId,visibility,access,false)).orElse(null);
        if(file==null) return null;


        if(folderId!=null){
            final var folderExists=folderRepository.exists(FileMgmtSpecification.getFoldersBy(ownerId,folderId,null,Visibility.PUBLIC,false));
            if(!folderExists) throw new Exception("Folder does not exists");
        }
        return ExtFile.fromFile(file);
    }

    @Transactional(readOnly = true)
    public ExtFile getFileById(String ownerId, String fileId) throws ApiException {
        if(fileId==null) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid file id");
        final var file=fileRepository.findOne(FileMgmtSpecification.getFilesBy(ownerId,fileId,null,Visibility.PUBLIC,false)).orElse(null);
        if(file==null) return null;


        if(file.getFolderId()!=null){
            final var folderExists=folderRepository.exists(FileMgmtSpecification.getFoldersBy(ownerId,file.getFolderId(),null,Visibility.PUBLIC,false));
            if(!folderExists) throw new ApiException(HttpStatus.BAD_REQUEST,"Folder does not exists");
        }
        return ExtFile.fromFile(file);
    }

    @Transactional(readOnly = true)
    public ExtFile getFileById(String fileId) throws ApiException {
        if(fileId==null) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid file id");
        final var file=fileRepository.findByIdAndIsDeleted(fileId,false).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"file not found"));
        return ExtFile.fromFile(file);
    }

    @Transactional(readOnly = true)
    public ExtFile getFileByName(String ownerId, String name) throws ApiException {
        if(name==null) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid file name");
        final var file=fileRepository.findOne(FileMgmtSpecification.getFilesBy(ownerId,null,name,Visibility.PUBLIC,false)).orElse(null);
        if(file==null) return null;


        if(file.getFolderId()!=null){
            final var folderExists=folderRepository.exists(FileMgmtSpecification.getFoldersBy(ownerId,file.getFolderId(),null,Visibility.PUBLIC,false));
            if(!folderExists) throw new ApiException(HttpStatus.BAD_REQUEST,"Folder does not exists");
        }
        return ExtFile.fromFile(file);
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
                .visibility(createFolderRequest.getVisibility()!=null ? createFolderRequest.getVisibility():Visibility.PRIVATE)
                .password(createFolderRequest.getPassword())
                .build();
        final var savedFolder=folderRepository.saveAndFlush(newFolder);
        return folderToDto(savedFolder);
    }

    @Transactional(readOnly = false)
    public ExtFile createFile(String ownerId, CreateFileRequest createFileRequest, MultipartFile multipartFile) throws Exception {
        final var originalFileName=multipartFile.getOriginalFilename();
        if(originalFileName==null) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid file name");
        final var extension= FileExtension.fromValue(extractExtension(originalFileName));
        final var key=createFileRequest.getName()!=null ? createFileRequest.getName()+"."+extension.getValue() : multipartFile.getOriginalFilename();
        if(key==null) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid file name");

        final var extensionMapping=Constants.allowedExtensions.get(extension.getValue());
        if(extensionMapping==null) throw new ApiException(HttpStatus.BAD_REQUEST,"file type not supported");

        if(createFileRequest.getFolderId()!=null && !folderRepository.existsByOwnerIdAndId(ownerId, createFileRequest.getFolderId())){
            throw new ApiException(HttpStatus.BAD_REQUEST,"folder does not exists");
        }

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
                .access(createFileRequest.getAccess())
                .videoSource(createFileRequest.getVideoSource())
                .visibility(createFileRequest.getVisibility())
                .build();
        final var savedFile=fileRepository.save(newFile);
        return ExtFile.fromFile(savedFile);
    }

    public void deleteFile(String ownerId, String fileId, Boolean permanentDelete) throws ApiException {
        final var file=fileRepository.findByOwnerIdAndId(ownerId,fileId).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"File not found"));
        if(file.isDeleted()) throw new ApiException(HttpStatus.NOT_FOUND,"File not found");
        if(permanentDelete!=null && permanentDelete.equals(Boolean.TRUE)){
            deleteFilePermanently(file);
        } else{
            file.setDeleted(true);
            fileRepository.save(file);
        }
    }

    public void deleteFolder(String ownerId, String folderId, Boolean permanentDelete) throws ApiException {
        final var folder=folderRepository.findByOwnerIdAndId(ownerId,folderId).orElseThrow(()->new ApiException(HttpStatus.NOT_FOUND,"folder not found"));
        if(folder.isDeleted()) throw new ApiException(HttpStatus.NOT_FOUND,"File not found");

        if(permanentDelete!=null && permanentDelete.equals(Boolean.TRUE)){
            //dangerous -> delete all folders and files in folder
            deleteFolderPermanently(folder);
        } else{
            //just mark folder/nested folder/files deleted
            softDeleteFolderPermanently(folder);
        }
    }

    public void deleteFilePermanently(File file){
        s3StorageService.deleteObject(file.getS3Key());
        fileRepository.delete(file);
    }

    public void deleteFolderPermanently(final Folder folder){
        final var nestedFolders=folderRepository.findAllByOwnerIdAndParentId(folder.getOwnerId(), folder.getId());
        nestedFolders.forEach(this::deleteFolderPermanently);
        final var files=fileRepository.findAllByOwnerIdAndFolderId(folder.getOwnerId(), folder.getId());
        files.forEach(this::deleteFilePermanently);
        folderRepository.delete(folder);
    }
    public void softDeleteFolderPermanently(final Folder folder){
        final var nestedFolders=folderRepository.findAllByOwnerIdAndParentId(folder.getOwnerId(), folder.getId());
        nestedFolders.forEach(this::softDeleteFolderPermanently);
        final var files=fileRepository.findAllByOwnerIdAndFolderId(folder.getOwnerId(), folder.getId());
        files.forEach((file)->file.setDeleted(true));
        if(!files.isEmpty()) fileRepository.saveAll(files);

        folder.setDeleted(true);
        folderRepository.save(folder);
    }

    @Transactional(readOnly = false)
    public ExtFile createFileFromContent(String ownerId, CreateFileFromContentRequest createFileFromContentRequest) throws Exception {
        final var fileName=createFileFromContentRequest.getFileName();
        if (fileName==null || !fileName.endsWith(".md")) {
            throw new ApiException(HttpStatus.BAD_REQUEST,"fileName must end with md");
        }

        if(createFileFromContentRequest.getContent()==null){
            throw new ApiException(HttpStatus.BAD_REQUEST,"file content cannot be empty");
        }

        if(createFileFromContentRequest.getFolderId()!=null && !folderRepository.existsByOwnerIdAndId(ownerId, createFileFromContentRequest.getFolderId())){
            throw new ApiException(HttpStatus.BAD_REQUEST,"folder does not exists");
        }

        final var allowOverrider=createFileFromContentRequest.getReplaceIfExists()!=null && createFileFromContentRequest.getReplaceIfExists().equals(Boolean.TRUE);
        final var fileExists=fileRepository.findByOwnerIdAndFolderIdAndName(ownerId,createFileFromContentRequest.getFolderId(), fileName);
        if(!allowOverrider && fileExists.isPresent()) throw new ApiException(HttpStatus.BAD_REQUEST,"file with same name cannot be created.");

        final var file=utilityService.contentToFile(createFileFromContentRequest.getContent(), fileName);
        final var mimeType=Files.probeContentType(file.toPath());

        try{
            final var uploadedFile=s3StorageService.uploadFile(file,fileName);
            if(fileExists.isPresent()){
                final var exFile=fileExists.get();
                exFile.setFileSize(file.length());
                exFile.setFolderId(createFileFromContentRequest.getFolderId());
                final var savedFile=fileRepository.save(exFile);
                return ExtFile.fromFile(savedFile);
            }else{
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
                return ExtFile.fromFile(savedFile);
            }
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

    @Transactional(readOnly = true)
    public List<File> findFilesBy(String ownerId, String folderId, @Min(0) long offset, @Min(1) long limit, Visibility visibility,List<FileAccess> accesses, Map<String, Sort.Direction> sort,Boolean isDeleted) throws ApiException {
        if(accesses==null) accesses=List.of();
        List<String> allowedSortColumns = List.of("name");
        StringBuilder sql = new StringBuilder("SELECT t FROM File as t WHERE ownerId = :ownerId");

        if (folderId == null) {
            sql.append(" AND folderId IS NULL");
        } else {
            sql.append(" AND folderId = :folderId");
        }

        if (isDeleted == null) {
            sql.append(" AND isDeleted = false");
        } else {
            sql.append(" AND isDeleted = :isDeleted");
        }

        if(visibility!=null){
            sql.append(" AND visibility = :visibility");
        }

        if(!accesses.isEmpty()){
            sql.append(" AND access IN (:access)");
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

        TypedQuery<File> query = entityManager.createQuery(sql.toString(), File.class);
        query.setParameter("ownerId", ownerId);
        if (!accesses.isEmpty()) query.setParameter("access",accesses);
        if (folderId != null) query.setParameter("folderId", folderId);
        if (visibility != null) query.setParameter("visibility", visibility);
        if (isDeleted != null) query.setParameter("isDeleted", isDeleted);
        query.setParameter("offset", offset);
        query.setParameter("limit", limit);

        return query.getResultList();
    }
}