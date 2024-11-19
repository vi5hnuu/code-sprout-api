package com.vi5hnu.codesprout.services.problemArchive;

import com.vi5hnu.codesprout.commons.Pageable;
import com.vi5hnu.codesprout.dto.CreateProblem;
import com.vi5hnu.codesprout.entity.ProblemArchive;
import com.vi5hnu.codesprout.repository.ProblemArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemArchiveService {

    private final ProblemArchiveRepository problemArchiveRepository;

    @Transactional(readOnly = true)
    public Pageable<ProblemArchive> getProblems(int pageNo, int limit) {
        PageRequest pageable = PageRequest.of(pageNo - 1, limit); // Page index is 0-based in Spring Data
        final var page=problemArchiveRepository.findAll(pageable);
        return new Pageable<>(page.get().toList(),pageNo,page.getTotalPages());
    }

    public ProblemArchive createProblem(CreateProblem problem) {
        final var newProblem=ProblemArchive
                .builder()
                .title(problem.getTitle())
                .description(problem.getDescription())
                .language(problem.getLanguage())
                .category(problem.getCategory())
                .filePath(problem.getFilePath())
                .build();

        return problemArchiveRepository.save(newProblem);
    }
}