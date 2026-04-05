package com.vi5hnu.codesprout.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vi5hnu.codesprout.entity.UserSubmission;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import com.vi5hnu.codesprout.enums.SubmissionStatus;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserSubmissionDto {
    private String id;
    private String problemId;
    private ProblemLanguage language;
    private SubmissionStatus status;
    private boolean isOfficial;
    private Timestamp submittedAt;

    public static UserSubmissionDto from(UserSubmission s) {
        return UserSubmissionDto.builder()
                .id(s.getId())
                .problemId(s.getProblemId())
                .language(s.getLanguage())
                .status(s.getStatus())
                .isOfficial(s.isOfficial())
                .submittedAt(s.getSubmittedAt())
                .build();
    }
}
