package com.vi5hnu.codesprout.entity;

import com.vi5hnu.codesprout.enums.LeadType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = Lead.TABLE_NAME)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead {
    public final static String PREFIX = "LED";
    public final static String TABLE_NAME = "leads";

    @Id
    @Column(length = 32)
    private String id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false,length = 100)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LeadType source = LeadType.PORTFOLIO;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void assignId() {
        if (this.id == null) this.id = (PREFIX + UUID.randomUUID().toString().replace("_","")).substring(0,32);
    }
}
