package com.vi5hnu.codesprout.entity;

import com.vi5hnu.codesprout.enums.BookmarkType;
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
@Table(
    name = UserBookmark.TABLE_NAME,
    uniqueConstraints = @UniqueConstraint(
        name = "uk_bookmark_type_target",
        columnNames = {"user_id", "type", "target_id"}
    )
)
public class UserBookmark {
    public static final String TABLE_NAME = "bookmark";
    public static final String ID_PREFIX  = "BKM";

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private BookmarkType type;

    /** ID of the bookmarked resource (problem ID, article ID, etc.) */
    @Column(name = "target_id", nullable = false)
    private String targetId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @PrePersist
    private void beforeSave() {
        if (id == null) id = IdGenerators.generateIdWithPrefix(ID_PREFIX);
    }
}
