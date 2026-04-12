package com.vi5hnu.codesprout.entity;

import com.vi5hnu.codesprout.enums.ProblemLanguage;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
@Entity
@Table(name = ProblemTemplate.TABLE_NAME,
    uniqueConstraints = @UniqueConstraint(
        name = "uk_template_problem_lang",
        columnNames = {"problem_id", "language"}
    )
)
public class ProblemTemplate extends BaseDomain {

    public static final String TABLE_NAME = "problem_template";
    public static final String ID_PREFIX  = "TPL";

    @Column(name = "problem_id", nullable = false, updatable = false)
    private String problemId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private ProblemLanguage language;

    @Column(name = "template_code", nullable = false, columnDefinition = "TEXT")
    private String templateCode;

    @PrePersist
    private void beforeSave() {
        initId(ID_PREFIX);
    }
}
