package com.vi5hnu.codesprout.services.problemArchive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vi5hnu.codesprout.commons.Pageable;
import com.vi5hnu.codesprout.enums.ProblemCategory;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import com.vi5hnu.codesprout.models.ProblemArchiveDto;
import com.vi5hnu.codesprout.models.dto.CreateProblem;
import com.vi5hnu.codesprout.entity.ProblemArchive;
import com.vi5hnu.codesprout.repository.ProblemArchiveRepository;
import com.vi5hnu.codesprout.specifications.ProblemArchiveSpecification;
import lombok.RequiredArgsConstructor;
import org.hibernate.Filter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProblemArchiveService {

    private final ProblemArchiveRepository problemArchiveRepository;

    @Transactional(readOnly = true)
    public Pageable<ProblemArchiveDto> getProblems(int pageNo, int limit, ProblemLanguage language, ProblemCategory category) {
        PageRequest pageable = PageRequest.of(pageNo - 1, limit); // Page index is 0-based in Spring Data
        Specification<ProblemArchive> langSpec = ProblemArchiveSpecification.hasLanguage(language)
                .and(ProblemArchiveSpecification.hasCategory(category));
        final var page=problemArchiveRepository.findAll(langSpec,pageable);
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