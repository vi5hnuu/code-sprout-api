package com.vi5hnu.codesprout.services.problemArchive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vi5hnu.codesprout.commons.Pageable;
import com.vi5hnu.codesprout.entity.ProblemTag;
import com.vi5hnu.codesprout.entity.ProblemTagAssociation;
import com.vi5hnu.codesprout.enums.ProblemDifficulty;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import com.vi5hnu.codesprout.models.ProblemArchiveDto;
import com.vi5hnu.codesprout.models.ProblemImage;
import com.vi5hnu.codesprout.models.ProblemTagAssociationDto;
import com.vi5hnu.codesprout.models.ProblemTagDto;
import com.vi5hnu.codesprout.models.dto.CreateProblemTagDto;
import com.vi5hnu.codesprout.models.dto.ProblemInfo;
import com.vi5hnu.codesprout.entity.ProblemArchive;
import com.vi5hnu.codesprout.models.dto.ProblemInfoWithPath;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemArchiveService {

    private static final Logger log = LoggerFactory.getLogger(ProblemArchiveService.class);
    private final ProblemArchiveRepository problemArchiveRepository;
    private final ProblemTagRepository problemTagRepository;
    private final ProblemTagAssociationRepository problemTagAssociationRepository;
    private final S3StorageService s3StorageService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Pageable<ProblemArchiveDto> getProblems(int pageNo, int limit, ProblemLanguage language, ProblemDifficulty difficulty) {
        PageRequest pageable = PageRequest.of(pageNo - 1, limit); // Page index is 0-based in Spring Data
        Specification<ProblemArchive> langSpec = ProblemArchiveSpecification.hasLanguage(language)
                .and(ProblemArchiveSpecification.hasDifficulty(difficulty));
        final var page=problemArchiveRepository.findAll(langSpec,pageable);
        return new Pageable<>(page.get().map(problemArchive -> {
            try {
                return fromProblemArchive(problemArchive);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList(),pageNo,page.getTotalPages());
    }

    public ProblemArchive createProblem(ProblemInfo problem, MultipartFile file,String filePath) throws Exception {
        if(file!=null){
            final var por=s3StorageService.uploadFile(file);
            if(!por.sdkHttpResponse().isSuccessful()) throw new Exception("Failed to upload file");
            filePath = s3StorageService.uploadedFilePath(file.getOriginalFilename());; // Construct the file's URL
        }
        if(filePath==null) throw new Exception("No Filepath found");
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
                .problemImages(objectMapper.writeValueAsString(problem.getProblemImages()))
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
                return fromProblemArchive(problem);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList(),pageNo,page.getTotalPages());
    }

    public ProblemTagDto createProblemTag(CreateProblemTagDto tagInfo) {
        final var problemTag=ProblemTag.builder()
                .title(tagInfo.getTitle())
                .description(tagInfo.getDescription())
                .imageUrl(tagInfo.getImageUrl())
                .build();
        final var savedTag=problemTagRepository.save(problemTag);

        return new ProblemTagDto(savedTag);
    }

    public ProblemTagAssociationDto addProblemToTag(String tagId, String problemId) throws Exception {
        if(!problemTagRepository.existsById(tagId) || !problemArchiveRepository.existsById(problemId)){
            throw new Exception("invalid tagId/problemId");
        }
        final var problemTagAccosiation=ProblemTagAssociation.builder()
                .tagId(tagId)
                .problemId(problemId)
                .build();
        final var savedAssociation=problemTagAssociationRepository.save(problemTagAccosiation);
        return new ProblemTagAssociationDto(savedAssociation);
    }

    public List<ProblemArchiveDto> createProblems(List<ProblemInfoWithPath> problems) throws JsonProcessingException {
        final List<ProblemArchive> problemArchives=new ArrayList<>();
        for(final ProblemInfoWithPath problemInfoWithPath : problems){
            final var arcive=ProblemArchive.builder()
                            .title(problemInfoWithPath.getTitle())
                            .description(problemInfoWithPath.getDescription())
                            .language(problemInfoWithPath.getLanguage())
                            .difficulty(problemInfoWithPath.getDifficulty())
                            .filePath(problemInfoWithPath.getFilePath())
                            .platforms(problemInfoWithPath.getPlatforms())
                            .problemImages(objectMapper.writeValueAsString(problemInfoWithPath.getProblemImages()))
                            .build();
            problemArchives.add(arcive);
        }
        final var savedArchives=problemArchiveRepository.saveAll(problemArchives);
        return savedArchives.stream().map((savedArchive)-> {
            try {
                return fromProblemArchive(savedArchive);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    private ProblemArchiveDto fromProblemArchive(ProblemArchive problemArchive) throws JsonProcessingException {
        final var problemImages=objectMapper.readValue(problemArchive.getProblemImages(),new TypeReference<List<ProblemImage>>() {});
        return ProblemArchiveDto.builder()
                .id(problemArchive.getId())
                .title(problemArchive.getTitle())
                .description(problemArchive.getDescription())
                .language(problemArchive.getLanguage())
                .difficulty(problemArchive.getDifficulty())
                .platforms(problemArchive.getPlatforms())
                .filePath(problemArchive.getFilePath())
                .problemImages(problemImages==null ? Collections.emptyList() : problemImages)
                .build();
    }

    @Transactional
    public List<ProblemTagAssociationDto> addAllProblemsToTag(String tagId, List<String> problemIds) throws Exception {
        final List<ProblemTagAssociation> problemTagAssociations=new ArrayList<>();

        if(!problemTagRepository.existsById(tagId)){
            throw new Exception("invalid tagId");
        }
        for(final String problemId : problemIds){
            if(!problemArchiveRepository.existsById(problemId)){
                throw new Exception("invalid tagId/problemId");
            }
            final var problemTagAccosiation=ProblemTagAssociation.builder()
                    .tagId(tagId)
                    .problemId(problemId)
                    .build();
            problemTagAssociations.add(problemTagAccosiation);
        }
        final var savedAssociations=problemTagAssociationRepository.saveAll(problemTagAssociations);
        return savedAssociations.stream().map(ProblemTagAssociationDto::new).toList();
    }
}