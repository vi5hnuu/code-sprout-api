package com.vi5hnu.codesprout.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = ProblemTag.TABLE_NAME)
public class ProblemTag extends BaseDomain {
    public static final String PREFIX     = "TID";
    public static final String TABLE_NAME = "tag";

    private String title;
    private String description;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @PrePersist
    public void prePersist() {
        initId(PREFIX);
    }
}
