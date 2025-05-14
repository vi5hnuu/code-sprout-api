package com.vi5hnu.codesprout.repository;

import com.vi5hnu.codesprout.entity.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, String>, JpaSpecificationExecutor<File> {
    Page<File> findAllByOwnerIdAndFolderId(String ownerId, String folderId, Pageable pageable);
    Optional<File> findByOwnerIdAndFolderIdAndId(String ownerId, String folderId, String fileId);
    Optional<File> findByOwnerIdAndFolderIdAndName(String ownerId, String folderId, String fileName);
    Optional<File> findByOwnerIdAndId(String ownerId, String fileId);
    Optional<File> findByOwnerIdAndName(String ownerId, String fileName);
    boolean existsByOwnerIdAndFolderIdAndName(String ownerId,String folderId,String name);
    long countByOwnerIdAndFolderId(String ownerId, String folderId);
}