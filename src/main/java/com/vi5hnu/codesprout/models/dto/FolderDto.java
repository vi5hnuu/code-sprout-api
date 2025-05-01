package com.vi5hnu.codesprout.models.dto;

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
public class FolderDto {
    private String id;
    private String ownerId;
    private String name;
    private String description;
    private String parentId;
    private String password;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}