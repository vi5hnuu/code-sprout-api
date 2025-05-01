package com.vi5hnu.codesprout.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;
@Entity
@Table(name = Folder.TABLE_NAME, indexes = {
        @Index(name = "idx_owner_parent", columnList = "owner_id, parent_id"),
        @Index(name = "idx_owner_folder_name", columnList = "owner_id, name"),
        @Index(name = "idx_owner", columnList = "owner_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Folder {
    public final static String PREFIX = "FOL";
    public final static String TABLE_NAME = "folder";

    @Id
    @Column(length = 32)
    private String id;

    @Column(name = "owner_id", length = 32, nullable = false)
    private String ownerId;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(length = 255, nullable = true)
    private String description;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "is_deleted")
    @Builder.Default
    private boolean isDeleted = false;

    @Column(length = 255)
    private String password;

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