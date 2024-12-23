package com.vi5hnu.codesprout.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vi5hnu.codesprout.enums.ProblemDifficulty;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
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
@Table(name = ProblemTag.TABLE_NAME)
public class ProblemTag {
    public final static String PREFIX = "TID";
    public final static String TABLE_NAME = "tag";

    @Id
    private String id;
    private String title;
    private String description;

    @Column(name = "image_url",nullable = false)
    private String imageUrl;

    @PrePersist
    public void assignId() {
        if (this.id == null) this.id = (PREFIX + UUID.randomUUID().toString().replace("_","")).substring(0,32);
    }
}