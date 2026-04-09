package com.vi5hnu.codesprout.repository;

import com.vi5hnu.codesprout.entity.FileView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileViewRepository extends JpaRepository<FileView, String> {

    long countByFileId(String fileId);

    @Query("SELECT COUNT(DISTINCT COALESCE(v.viewerId, v.ipAddress)) FROM FileView v WHERE v.fileId = :fileId")
    long countUniqueViewersByFileId(@Param("fileId") String fileId);
}
