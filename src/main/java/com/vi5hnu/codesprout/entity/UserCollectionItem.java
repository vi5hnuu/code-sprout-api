package com.vi5hnu.codesprout.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
@Entity
@Table(name = UserCollectionItem.TABLE_NAME,
    uniqueConstraints = @UniqueConstraint(
        name = "uk_col_item",
        columnNames = {"collection_id", "problem_id"}
    )
)
public class UserCollectionItem {

    public static final String TABLE_NAME = "user_collection_item";
    public static final String ID_PREFIX  = "CIT";

    @Id
    private String id;

    @Column(name = "collection_id", nullable = false, updatable = false)
    private String collectionId;

    @Column(name = "problem_id", nullable = false, updatable = false)
    private String problemId;

    @CreationTimestamp
    @Column(name = "added_at", updatable = false)
    private Timestamp addedAt;

    @PrePersist
    private void beforeSave() {
        if (id == null) id = com.vi5hnu.codesprout.utils.IdGenerators.prefixedUlid(ID_PREFIX);
    }
}
