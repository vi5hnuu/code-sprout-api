package com.vi5hnu.codesprout.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vi5hnu.codesprout.enums.ProblemDifficulty;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import com.vi5hnu.codesprout.enums.Visibility;
import com.vi5hnu.codesprout.models.ProblemPlatform;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;


//CREATE TABLE file (
//        id VARCHAR(32) PRIMARY KEY,
//owner_id VARCHAR(32) NOT NULL,
//folder_id VARCHAR(32) DEFAULT NULL,
//name VARCHAR(255) NOT NULL,
//file_extension VARCHAR(20) NOT NULL,
//mime_type VARCHAR(50) CHECK (
//        (file_extension = 'txt' AND mime_type = 'text/plain') OR
//        (file_extension = 'md' AND mime_type = 'text/markdown')
//                         ) NOT NULL,
//s3_key VARCHAR(1024) NOT NULL,
//file_size BIGINT UNSIGNED NOT NULL, --size in bytes
//visibility ENUM('public', 'private') DEFAULT 'private',
//is_deleted BOOLEAN DEFAULT FALSE,
//created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
//
//CONSTRAINT fk_files_folder FOREIGN KEY (folder_id) REFERENCES folder(id)  ON DELETE RESTRICT,  -- Prevent folder deletion if it contains any files
//INDEX idx_owner_folder (owner_id, folder_id),
//INDEX idx_s3_key (s3_key)  -- Index added for quick lookups by S3 key
//);

@Entity
@Table(name = File.TABLE_NAME, indexes = {
        @Index(name = "idx_owner_folder", columnList = "owner_id, folder_id"),
        @Index(name = "idx_s3_key", columnList = "s3_key")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File {
    public final static String PREFIX = "FID";
    public final static String TABLE_NAME = "file";

    @Id
    @Column(length = 32)
    private String id;

    @Column(name = "owner_id", length = 32, nullable = false)
    private String ownerId;

    @Column(name = "folder_id")
    private String folderId;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(name = "file_extension", length = 20, nullable = false)
    private String fileExtension;

    @Column(name = "mime_type", length = 50, nullable = false)
    private String mimeType;

    @Column(name = "s3_key", length = 1024, nullable = false)
    private String s3Key;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;//in bytes

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Visibility visibility = Visibility.PRIVATE;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;


    @PrePersist
    public void assignId() {
        if (this.id == null) this.id = (PREFIX + UUID.randomUUID().toString().replace("_","")).substring(0,32);
    }
}
