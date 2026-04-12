package com.vi5hnu.codesprout.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
@Entity
@Table(name = UserCollection.TABLE_NAME)
public class UserCollection extends BaseDomain {

    public static final String TABLE_NAME = "user_collection";
    public static final String ID_PREFIX  = "COL";

    @Column(name = "user_id", nullable = false, updatable = false)
    private String userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @PrePersist
    private void beforeSave() {
        initId(ID_PREFIX);
    }
}
