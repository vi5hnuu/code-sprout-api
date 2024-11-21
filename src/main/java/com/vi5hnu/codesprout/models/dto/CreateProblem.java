package com.vi5hnu.codesprout.models.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vi5hnu.codesprout.enums.ProblemCategory;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import com.vi5hnu.codesprout.models.ProblemPlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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

    @NotNull(message = "problem platforms cannot be null")
    private List<ProblemPlatform> platforms;
}