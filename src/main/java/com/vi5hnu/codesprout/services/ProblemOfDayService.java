package com.vi5hnu.codesprout.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vi5hnu.codesprout.entity.ProblemArchive;
import com.vi5hnu.codesprout.entity.ProblemOfDay;
import com.vi5hnu.codesprout.models.*;
import com.vi5hnu.codesprout.repository.ProblemArchiveRepository;
import com.vi5hnu.codesprout.repository.ProblemOfDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProblemOfDayService {

    private final ProblemOfDayRepository podRepository;
    private final ProblemArchiveRepository problemRepo;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Optional<ProblemOfDayDto> getToday() {
        return podRepository.findByScheduledDate(LocalDate.now())
                .flatMap(pod -> problemRepo.findById(pod.getProblemId())
                        .map(p -> ProblemOfDayDto.builder()
                                .scheduledDate(pod.getScheduledDate())
                                .problem(toProblemDto(p))
                                .build()));
    }

    /** Admin: schedule a problem for a given date (upsert). */
    @Transactional
    public ProblemOfDayDto schedule(String problemId, LocalDate date) {
        ProblemOfDay pod = podRepository.findByScheduledDate(date)
                .orElseGet(() -> ProblemOfDay.builder().scheduledDate(date).build());
        pod.setProblemId(problemId);
        podRepository.save(pod);
        ProblemArchive p = problemRepo.findById(problemId)
                .orElseThrow(() -> new RuntimeException("Problem not found"));
        return ProblemOfDayDto.builder().scheduledDate(date).problem(toProblemDto(p)).build();
    }

    private ProblemArchiveDto toProblemDto(ProblemArchive p) {
        List<ProblemImage> images;
        try {
            String raw = p.getProblemImages();
            images = (raw == null || raw.isBlank()) ? Collections.emptyList()
                    : objectMapper.readValue(raw, new TypeReference<>() {});
        } catch (Exception e) { images = Collections.emptyList(); }
        List<ProblemPlatform> platforms;
        try { platforms = p.getPlatforms(); } catch (Exception e) { platforms = Collections.emptyList(); }
        return ProblemArchiveDto.builder()
                .id(p.getId()).slug(p.getSlug()).title(p.getTitle())
                .description(p.getDescription()).language(p.getLanguage())
                .difficulty(p.getDifficulty()).filePath(p.getFilePath())
                .platforms(platforms).problemImages(images).build();
    }
}
