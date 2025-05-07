package com.vi5hnu.codesprout.models;

import lombok.*;

import java.sql.Timestamp;

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