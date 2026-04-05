package com.vi5hnu.codesprout.models.judge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class JudgeJobResponse {
    private String jobId;
    private String status;     // pending | running | done | failed
    private String verdict;    // AC | WA | TLE | MLE | RE | CE | IE | null
    private Integer totalTimeMs;
    private List<JudgeTestResult> results;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class JudgeTestResult {
        private String id;
        private int index;
        private String stdin;
        private String expected;
        private String stdout;
        private String stderr;
        private String verdict;
        private Integer timeMs;
    }
}
