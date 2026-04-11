package com.vi5hnu.codesprout.services;

import com.vi5hnu.codesprout.entity.UserSubmission;
import com.vi5hnu.codesprout.enums.ProblemDifficulty;
import com.vi5hnu.codesprout.enums.SubmissionStatus;
import com.vi5hnu.codesprout.models.UserStatsDto;
import com.vi5hnu.codesprout.repository.ProblemArchiveRepository;
import com.vi5hnu.codesprout.repository.UserSubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final UserSubmissionRepository submissionRepo;
    private final ProblemArchiveRepository problemRepo;

    @Transactional(readOnly = true)
    public UserStatsDto getStats(String userId) {
        List<UserSubmission> all = submissionRepo.findByUserIdOrderBySubmittedAtDesc(
                userId, org.springframework.data.domain.Pageable.unpaged()).getContent();

        // Accepted official submissions — distinct problems
        Set<String> acceptedIds = all.stream()
                .filter(s -> s.getStatus() == SubmissionStatus.ACCEPTED && s.isOfficial())
                .map(UserSubmission::getProblemId)
                .collect(Collectors.toSet());

        // Difficulty breakdown for accepted problems
        int easy = 0, medium = 0, hard = 0;
        if (!acceptedIds.isEmpty()) {
            var problems = problemRepo.findAllById(acceptedIds);
            for (var p : problems) {
                if (p.getDifficulty() == ProblemDifficulty.EASY)        easy++;
                else if (p.getDifficulty() == ProblemDifficulty.MEDIUM) medium++;
                else if (p.getDifficulty() == ProblemDifficulty.HARD)   hard++;
            }
        }

        // Activity map: date -> submission count (last 365 days)
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        Map<String, Integer> activityMap = new TreeMap<>();
        for (var s : all) {
            String day = s.getSubmittedAt().toInstant().atZone(ZoneOffset.UTC).toLocalDate().format(fmt);
            activityMap.merge(day, 1, Integer::sum);
        }

        // Language breakdown: language -> accepted count
        Map<String, Integer> langBreakdown = all.stream()
                .filter(s -> s.getStatus() == SubmissionStatus.ACCEPTED && s.isOfficial())
                .collect(Collectors.groupingBy(
                        s -> s.getLanguage().name(),
                        Collectors.collectingAndThen(
                                Collectors.mapping(UserSubmission::getProblemId, Collectors.toSet()),
                                Set::size)));

        // Streak calculation
        int[] streaks = computeStreaks(activityMap);

        return UserStatsDto.builder()
                .totalSolved(acceptedIds.size())
                .easySolved(easy)
                .mediumSolved(medium)
                .hardSolved(hard)
                .totalSubmissions(all.size())
                .currentStreak(streaks[0])
                .longestStreak(streaks[1])
                .activityMap(activityMap)
                .languageBreakdown(langBreakdown)
                .build();
    }

    /** Returns [currentStreak, longestStreak] from the date-keyed activity map. */
    private int[] computeStreaks(Map<String, Integer> activityMap) {
        if (activityMap.isEmpty()) return new int[]{0, 0};
        List<LocalDate> activeDays = activityMap.keySet().stream()
                .map(LocalDate::parse)
                .sorted(Comparator.reverseOrder())
                .toList();

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        int current = 0;
        LocalDate expected = today;
        for (LocalDate d : activeDays) {
            if (d.equals(expected) || d.equals(today) && current == 0) {
                current++;
                expected = d.minusDays(1);
            } else if (d.isBefore(expected)) {
                break;
            }
        }

        // Longest streak
        int longest = 0, run = 1;
        List<LocalDate> sorted = activityMap.keySet().stream().map(LocalDate::parse).sorted().toList();
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i).equals(sorted.get(i - 1).plusDays(1))) {
                run++;
                longest = Math.max(longest, run);
            } else {
                run = 1;
            }
        }
        longest = Math.max(longest, run);

        return new int[]{current, longest};
    }
}
