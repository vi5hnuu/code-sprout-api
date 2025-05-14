package com.vi5hnu.codesprout.repository;

import com.vi5hnu.codesprout.entity.Folder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, String>, JpaSpecificationExecutor<Folder> {

    Page<Folder> findAllByOwnerId(String ownerId, Pageable pageable);
    Page<Folder> findAllByOwnerIdAndParentId(String ownerId, String pId, Pageable pageable);
    Optional<Folder> findByOwnerIdAndName(String ownerId, String name);
    Optional<Folder> findByOwnerIdAndId(String ownerId, String id);

    boolean existsByOwnerIdAndId(String ownerId, String folderId);
    boolean existsByOwnerIdAndParentIdAndName(String ownerId, String parentFolderId, String name);
}