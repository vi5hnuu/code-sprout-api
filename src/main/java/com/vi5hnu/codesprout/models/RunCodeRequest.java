package com.vi5hnu.codesprout.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RunCodeRequest {
    @NotBlank  private String problemId;
    @NotBlank  private String problemSlug;
    @NotNull   private ProblemLanguage language;
    @NotBlank  private String code;
//    @NotEmpty
    private List<RunTestCase> testCases;
    private String customInput;
    private int timeLimitMs = 5_000;
    private int memLimitMb  = 256;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RunTestCase {
        @NotNull private String input;
        @NotNull private String expectedOutput;
    }
}
