package com.vi5hnu.codesprout.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
@Entity
@Table(name = ProblemHint.TABLE_NAME)
public class ProblemHint extends BaseDomain {

    public static final String TABLE_NAME = "problem_hint";
    public static final String ID_PREFIX  = "HNT";

    @Column(name = "problem_id", nullable = false, updatable = false)
    private String problemId;

    @Column(name = "hint_order", nullable = false)
    private int hintOrder;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @PrePersist
    private void beforeSave() {
        initId(ID_PREFIX);
    }
}
