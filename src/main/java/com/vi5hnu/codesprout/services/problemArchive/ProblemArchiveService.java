package com.vi5hnu.codesprout.services.problemArchive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vi5hnu.codesprout.commons.Pageable;
import com.vi5hnu.codesprout.entity.ProblemTag;
import com.vi5hnu.codesprout.entity.ProblemTagAssociation;
import com.vi5hnu.codesprout.enums.ProblemDifficulty;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import com.vi5hnu.codesprout.models.ProblemArchiveDto;
import com.vi5hnu.codesprout.models.ProblemTagDto;
import com.vi5hnu.codesprout.models.dto.ProblemInfo;
import com.vi5hnu.codesprout.entity.ProblemArchive;
import com.vi5hnu.codesprout.repository.ProblemArchiveRepository;
import com.vi5hnu.codesprout.repository.ProblemTagAssociationRepository;
import com.vi5hnu.codesprout.repository.ProblemTagRepository;
import com.vi5hnu.codesprout.specifications.ProblemArchiveSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProblemArchiveService {

    private static final Logger log = LoggerFactory.getLogger(ProblemArchiveService.class);
    private final ProblemArchiveRepository problemArchiveRepository;
    private final ProblemTagRepository problemTagRepository;
    private final ProblemTagAssociationRepository problemTagAssociationRepository;
    private final S3StorageService s3StorageService;

    @Transactional(readOnly = true)
    public Pageable<ProblemArchiveDto> getProblems(int pageNo, int limit, ProblemLanguage language, ProblemDifficulty difficulty) {
        PageRequest pageable = PageRequest.of(pageNo - 1, limit); // Page index is 0-based in Spring Data
        Specification<ProblemArchive> langSpec = ProblemArchiveSpecification.hasLanguage(language)
                .and(ProblemArchiveSpecification.hasDifficulty(difficulty));
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
                .difficulty(problem.getDifficulty())
                .filePath(filePath)
                .platforms(problem.getPlatforms())
                .build();
        return problemArchiveRepository.save(newProblem);
    }

    public Pageable<ProblemTagDto> getProblemTags(int pageNo, int pageSize) {
        PageRequest pageable = PageRequest.of(pageNo - 1, pageSize); // Page index is 0-based in Spring Data
        final var page=problemTagRepository.findAll(pageable);
        return new Pageable<>(
                page.get().map((tag)->new ProblemTagDto(tag.getId(),tag.getTitle(),tag.getDescription(),tag.getImageUrl())).toList(),
                pageNo,
                page.getTotalPages());
    }

    public Pageable<ProblemArchiveDto> getTagProblems(String tagId, int pageNo, int pageSize) {
        PageRequest pageable = PageRequest.of(pageNo - 1, pageSize); // Page index is 0-based in Spring Data
        final var page=problemTagAssociationRepository.findAllByTagId(tagId,pageable);
        final var problemIds=page.stream().map(ProblemTagAssociation::getProblemId).toList();
        final var problems=problemArchiveRepository.findAllById(problemIds);

        return new Pageable<>(problems.stream().map((problem)-> {
            try {
                return new ProblemArchiveDto(problem);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList(),pageNo,page.getTotalPages());
    }
}