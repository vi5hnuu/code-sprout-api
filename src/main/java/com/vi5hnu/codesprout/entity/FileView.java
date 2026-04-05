package com.vi5hnu.codesprout.entity;

import com.vi5hnu.codesprout.utils.IdGenerators;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = FileView.TABLE_NAME)
public class FileView {
    public static final String TABLE_NAME = "file_view";
    public static final String ID_PREFIX  = "FV";

    @Id
    private String id;

    @Column(name = "file_id", nullable = false)
    private String fileId;

    /** null for anonymous viewers */
    @Column(name = "viewer_id")
    private String viewerId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @CreationTimestamp
    @Column(name = "viewed_at", updatable = false)
    private Timestamp viewedAt;

    @PrePersist
    private void beforeSave() {
        if (id == null) id = IdGenerators.generateIdWithPrefix(ID_PREFIX);
    }
}
