package com.vi5hnu.codesprout.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.Map;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserStatsDto {
    private int totalSolved;
    private int easySolved;
    private int mediumSolved;
    private int hardSolved;
    private int totalSubmissions;
    private int currentStreak;       // consecutive days with an accepted submission
    private int longestStreak;
    private Map<String, Integer> activityMap;  // "YYYY-MM-DD" -> count of submissions that day
    private Map<String, Integer> languageBreakdown; // language -> accepted count
}
