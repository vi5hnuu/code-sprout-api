package com.vi5hnu.codesprout.entity;

import com.vi5hnu.codesprout.enums.ProblemLanguage;
import com.vi5hnu.codesprout.enums.SubmissionStatus;
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
@Table(name = UserSubmission.TABLE_NAME)
public class UserSubmission {
    public static final String TABLE_NAME = "user_submission";
    public static final String ID_PREFIX  = "SUB";

    @Id
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "problem_id", nullable = false)
    private String problemId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProblemLanguage language;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    /** true = full submit (all test cases), false = run (visible test cases only) */
    @Column(name = "is_official", nullable = false)
    @Builder.Default
    private boolean isOfficial = false;

    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private Timestamp submittedAt;

    @PrePersist
    private void beforeSave() {
        if (id == null) id = IdGenerators.generateIdWithPrefix(ID_PREFIX);
    }
}
