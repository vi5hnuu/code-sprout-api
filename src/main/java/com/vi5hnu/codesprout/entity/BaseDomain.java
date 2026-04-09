package com.vi5hnu.codesprout.entity;

import com.vi5hnu.codesprout.utils.IdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Abstract base for all JPA entities.
 * Provides a time-sortable ULID primary key and auto-managed createdAt/updatedAt timestamps.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseDomain {

    @Id
    private String id;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /** Call from subclass {@code @PrePersist}: assigns a prefixed time-sortable ID. */
    protected void initId(String prefix) {
        if (this.id == null) this.id = IdGenerators.prefixedUlid(prefix);
    }
}
