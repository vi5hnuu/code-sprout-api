package com.vi5hnu.codesprout.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vi5hnu.codesprout.entity.ProblemArchive;
import com.vi5hnu.codesprout.enums.ProblemDifficulty;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import lombok.*;

import java.util.List;

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
    private ProblemDifficulty difficulty;
    private List<ProblemPlatform> platforms;

    public ProblemArchiveDto(ProblemArchive problemArchive) throws JsonProcessingException {
        setId(problemArchive.getId());
        setTitle(problemArchive.getTitle());
        setDescription(problemArchive.getDescription());
        setLanguage(problemArchive.getLanguage());
        setDifficulty(problemArchive.getDifficulty());
        setPlatforms(problemArchive.getPlatforms());
        setFilePath(problemArchive.getFilePath());
    }
}