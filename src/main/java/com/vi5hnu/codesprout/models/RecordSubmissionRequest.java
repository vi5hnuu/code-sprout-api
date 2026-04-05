package com.vi5hnu.codesprout.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import com.vi5hnu.codesprout.enums.SubmissionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RecordSubmissionRequest {
    @NotBlank
    private String problemId;
    @NotNull
    private ProblemLanguage language;
    @NotNull
    private SubmissionStatus status;
    /** true = full submit, false = run */
    private boolean isOfficial;
}
