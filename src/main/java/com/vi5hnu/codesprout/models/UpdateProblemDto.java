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
public class UpdateProblemDto {
    private String title;
    private String slug;
    private String description;
    private ProblemLanguage language;
    private ProblemDifficulty difficulty;
    private String platforms;
    private String filePath;
    private List<ProblemImage> problemImages;
}
