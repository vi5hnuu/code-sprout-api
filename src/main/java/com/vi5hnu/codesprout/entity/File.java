package com.vi5hnu.codesprout.entity;

import com.vi5hnu.codesprout.enums.FileAccess;
import com.vi5hnu.codesprout.enums.FileExtension;
import com.vi5hnu.codesprout.enums.Visibility;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = File.TABLE_NAME, indexes = {
        @Index(name = "idx_owner_folder", columnList = "owner_id, folder_id")
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
    @Enumerated(EnumType.STRING)
    private FileExtension fileExtension;

    @Column(name = "mime_type", length = 50, nullable = false)
    private String mimeType;

    @Column(name = "s3_key", length = 1024, nullable = false)
    private String s3Key;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;//in bytes

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Builder.Default()
    private Visibility visibility = Visibility.PRIVATE;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Builder.Default()
    private FileAccess access = FileAccess.PREMIUM;

    @Column(name = "is_deleted")
    @Builder.Default
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
