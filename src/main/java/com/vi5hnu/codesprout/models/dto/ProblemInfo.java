package com.vi5hnu.codesprout.models.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vi5hnu.codesprout.enums.ProblemDifficulty;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProblemInfo {
    @NotBlank(message = "problem title cannot be blank")
    private String title;
    private String description;

    @NotNull(message = "problem language cannot be null")
    private ProblemLanguage language;

    @NotNull(message = "problem difficulty cannot be null")
    private ProblemDifficulty difficulty;

    @NotNull(message = "problem platforms cannot be null")
    private String platforms;
}