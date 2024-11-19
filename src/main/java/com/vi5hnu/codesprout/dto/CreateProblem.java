package com.vi5hnu.codesprout.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vi5hnu.codesprout.enums.ProblemCategory;
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
public class CreateProblem {
    @NotBlank(message = "problem title cannot be blank")
    private String title;
    private String description;

    @NotNull(message = "problem language cannot be null")
    private ProblemLanguage language;

    @NotNull(message = "problem category cannot be null")
    private ProblemCategory category;

    @NotBlank(message = "problem filePath cannot be blank")
    private String filePath;
}