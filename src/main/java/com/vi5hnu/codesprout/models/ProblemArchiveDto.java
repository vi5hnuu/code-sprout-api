package com.vi5hnu.codesprout.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
    private String slug;
    private String title;
    private String description;
    private String filePath;
    private ProblemLanguage language;
    private ProblemDifficulty difficulty;
    private List<ProblemPlatform> platforms;
    private List<ProblemImage> problemImages;
}