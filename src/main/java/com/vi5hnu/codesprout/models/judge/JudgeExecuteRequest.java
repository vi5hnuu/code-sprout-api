package com.vi5hnu.codesprout.models.judge;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class JudgeExecuteRequest {
    private String language;
    private String code;
    private int timeLimitMs;
    private int memLimitMb;
    private List<JudgeTestCase> testCases;

    @Getter
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class JudgeTestCase {
        private String stdin;
        private String expected;
    }
}
