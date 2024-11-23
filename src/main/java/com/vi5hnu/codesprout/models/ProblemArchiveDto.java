package com.vi5hnu.codesprout.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vi5hnu.codesprout.entity.ProblemArchive;
import com.vi5hnu.codesprout.enums.ProblemCategory;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProblemArchiveDto {
    private String id;
    private String title;
    private String description;
    private String filePath;
    private ProblemLanguage language;
    private ProblemCategory category;
    private List<ProblemPlatform> platforms;

    public ProblemArchiveDto(ProblemArchive problemArchive) throws JsonProcessingException {
        setId(problemArchive.getId());
        setTitle(problemArchive.getTitle());
        setDescription(problemArchive.getDescription());
        setLanguage(problemArchive.getLanguage());
        setCategory(problemArchive.getCategory());
        setPlatforms(problemArchive.getPlatforms());
        setFilePath(problemArchive.getFilePath());
    }
}