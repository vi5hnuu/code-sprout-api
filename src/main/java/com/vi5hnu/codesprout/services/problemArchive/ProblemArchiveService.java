package com.vi5hnu.codesprout.services.problemArchive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vi5hnu.codesprout.commons.Pageable;
import com.vi5hnu.codesprout.models.ProblemArchiveDto;
import com.vi5hnu.codesprout.models.dto.CreateProblem;
import com.vi5hnu.codesprout.entity.ProblemArchive;
import com.vi5hnu.codesprout.repository.ProblemArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProblemArchiveService {

    private final ProblemArchiveRepository problemArchiveRepository;

    @Transactional(readOnly = true)
    public Pageable<ProblemArchiveDto> getProblems(int pageNo, int limit) {
        PageRequest pageable = PageRequest.of(pageNo - 1, limit); // Page index is 0-based in Spring Data
        final var page=problemArchiveRepository.findAll(pageable);
        return new Pageable<>(page.get().map(problemArchive -> {
            try {
                return new ProblemArchiveDto(problemArchive);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList(),pageNo,page.getTotalPages());
    }

    public ProblemArchive createProblem(CreateProblem problem) {
        final var newProblem=ProblemArchive
                .builder()
                .title(problem.getTitle())
                .description(problem.getDescription())
                .language(problem.getLanguage())
                .category(problem.getCategory())
                .filePath(problem.getFilePath())
                .platforms(problem.getPlatforms())
                .build();

        return problemArchiveRepository.save(newProblem);
    }
}