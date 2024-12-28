package com.vi5hnu.codesprout.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vi5hnu.codesprout.enums.ProblemDifficulty;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import com.vi5hnu.codesprout.models.ProblemPlatform;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity()
@Table(name = ProblemArchive.TABLE_NAME)
public class ProblemArchive {
    public final static String PREFIX = "PID";
    public final static String TABLE_NAME = "problem_archive";

    @Id
    private String id;
    private String title;
    private String description;

    @Enumerated(EnumType.STRING) // Ensures the enum is stored as a string in the DB
    @Column(nullable = false)
    private ProblemLanguage language;

    @Enumerated(EnumType.STRING) // Ensures the enum is stored as a string in the DB
    @Column(nullable = false)
    private ProblemDifficulty difficulty;
    private String platforms;

    @Column(name = "file_path",nullable = false)
    private String filePath;

    private String problemImages;

    @PrePersist
    public void assignId() {
        if(platforms==null) platforms = "[]";
        if(problemImages==null) problemImages = "[]";
        if (this.id == null) this.id = (PREFIX + UUID.randomUUID().toString().replace("_","")).substring(0,32);
    }

    public List<ProblemPlatform> getPlatforms() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(this.platforms, new TypeReference<>() {
        });
    }
}