package com.vi5hnu.codesprout.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
@Entity
@Table(name = ProblemOfDay.TABLE_NAME,
    uniqueConstraints = @UniqueConstraint(
        name = "uk_pod_date",
        columnNames = {"scheduled_date"}
    )
)
public class ProblemOfDay {

    public static final String TABLE_NAME = "problem_of_day";
    public static final String ID_PREFIX  = "POD";

    @Id
    private String id;

    @Column(name = "problem_id", nullable = false)
    private String problemId;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @PrePersist
    private void beforeSave() {
        if (id == null) id = com.vi5hnu.codesprout.utils.IdGenerators.prefixedUlid(ID_PREFIX);
    }
}
