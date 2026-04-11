package com.vi5hnu.codesprout.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
@Entity
@Table(name = UserNote.TABLE_NAME,
    uniqueConstraints = @UniqueConstraint(
        name = "uk_note_user_problem",
        columnNames = {"user_id", "problem_id"}
    )
)
public class UserNote extends BaseDomain {

    public static final String TABLE_NAME = "user_note";
    public static final String ID_PREFIX  = "NOT";

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @Column(name = "problem_id", nullable = false, updatable = false)
    private String problemId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @PrePersist
    private void beforeSave() {
        initId(ID_PREFIX);
    }
}
