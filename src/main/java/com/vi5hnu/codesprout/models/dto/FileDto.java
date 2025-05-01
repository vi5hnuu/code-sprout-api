package com.vi5hnu.codesprout.models.dto;

import com.vi5hnu.codesprout.enums.FileExtension;
import com.vi5hnu.codesprout.enums.Visibility;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDto {
    private String id;
    private String ownerId;
    private String folderId;
    private String name;
    private FileExtension fileExtension;
    private String mimeType;
    private String s3Key;
    private Long fileSize;//in bytes
    private Visibility visibility = Visibility.PRIVATE;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
