package com.vi5hnu.codesprout.services.problemArchive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vi5hnu.codesprout.commons.Pageable;
import com.vi5hnu.codesprout.enums.ProblemCategory;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import com.vi5hnu.codesprout.models.ProblemArchiveDto;
import com.vi5hnu.codesprout.models.dto.ProblemInfo;
import com.vi5hnu.codesprout.entity.ProblemArchive;
import com.vi5hnu.codesprout.repository.ProblemArchiveRepository;
import com.vi5hnu.codesprout.specifications.ProblemArchiveSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProblemArchiveService {

    private static final Logger log = LoggerFactory.getLogger(ProblemArchiveService.class);
    private final ProblemArchiveRepository problemArchiveRepository;
    private final S3StorageService s3StorageService;

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

    public ProblemArchive createProblem(ProblemInfo problem, MultipartFile file) throws Exception {
        final var por=s3StorageService.uploadFile(file);
        if(!por.sdkHttpResponse().isSuccessful()) throw new Exception("Failed to upload file");
        String filePath = s3StorageService.uploadedFilePath(file.getOriginalFilename());; // Construct the file's URL
        final var exProbelm=problemArchiveRepository.findByTitle(problem.getTitle());
        if(exProbelm.isPresent()) return exProbelm.get();
        final var newProblem=ProblemArchive
                .builder()
                .title(problem.getTitle())
                .description(problem.getDescription())
                .language(problem.getLanguage())
                .category(problem.getCategory())
                .filePath(filePath)
                .platforms(problem.getPlatforms())
                .build();
        return problemArchiveRepository.save(newProblem);
    }

}