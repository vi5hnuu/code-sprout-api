package com.vi5hnu.codesprout.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
    name = ProblemTagAssociation.TABLE_NAME,
    uniqueConstraints = @UniqueConstraint(
        name = "uq_tag_problem",
        columnNames = {"tag_id", "problem_id"}
    )
)
public class ProblemTagAssociation {
    public static final String PREFIX     = "PTA";
    public static final String TABLE_NAME = "problem_tag_association";

    @Id
    private String id;

    @Column(name = "tag_id", nullable = false)
    private String tagId;

    @Column(name = "problem_id", nullable = false)
    private String problemId;

    @PrePersist
    public void assignId() {
        if (this.id == null) {
            this.id = (PREFIX + UUID.randomUUID().toString().replace("-", "")).substring(0, 32);
        }
    }
}
