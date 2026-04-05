package com.vi5hnu.codesprout.models.judge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** One streamed message from the judge WebSocket. Terminal frame has done=true. */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class JudgeAgentResult {
    private int index;
    private String verdict;
    private int timeMs;
    private String stdout;
    private String stderr;
    private boolean done;
}
