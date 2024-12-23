package com.vi5hnu.codesprout.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vi5hnu.codesprout.models.ProblemPlatform;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity()
@Table(name = ProblemTagAssociation.TABLE_NAME)
public class ProblemTagAssociation {
    public final static String PREFIX = "PTA";
    public final static String TABLE_NAME = "problem_tag_association";

    @Id
    private String id;

    @Column(name = "tag_id",nullable = false)
    private String tagId;

    @Column(name = "problem_id",nullable = false)
    private String problemId;

    @PrePersist
    public void assignId() {
        if (this.id == null) this.id = (PREFIX + UUID.randomUUID().toString().replace("_","")).substring(0,32);
    }
}