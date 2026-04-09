package com.vi5hnu.codesprout.services.problemArchive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vi5hnu.codesprout.commons.Pageable;
import com.vi5hnu.codesprout.configuration.CacheConfig;
import com.vi5hnu.codesprout.entity.ProblemTag;
import com.vi5hnu.codesprout.entity.ProblemTagAssociation;
import com.vi5hnu.codesprout.enums.ProblemDifficulty;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import com.vi5hnu.codesprout.models.*;
import com.vi5hnu.codesprout.entity.ProblemArchive;
import com.vi5hnu.codesprout.repository.ProblemArchiveRepository;
import com.vi5hnu.codesprout.repository.ProblemTagAssociationRepository;
import com.vi5hnu.codesprout.repository.ProblemTagRepository;
import com.vi5hnu.codesprout.services.S3StorageService;
import com.vi5hnu.codesprout.services.UtilityService;
import com.vi5hnu.codesprout.specifications.ProblemArchiveSpecification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemArchiveService {

    private static final Logger log = LoggerFactory.getLogger(ProblemArchiveService.class);
    private final ProblemArchiveRepository problemArchiveRepository;
    private final ProblemTagRepository problemTagRepository;
    private final ProblemTagAssociationRepository problemTagAssociationRepository;
    private final S3StorageService s3StorageService;
    private final ObjectMapper objectMapper;
    private final UtilityService utilityService;

    @Cacheable(value = CacheConfig.PROBLEMS_CACHE,
               key = "#pageNo + '-' + #limit + '-' + #language + '-' + #difficulty + '-' + #search")
    @Transactional(readOnly = true)
    public Pageable<ProblemArchiveDto> getProblems(int pageNo, int limit,
                                                    ProblemLanguage language,
                                                    ProblemDifficulty difficulty,
                                                    String search) {
        PageRequest pageable = PageRequest.of(pageNo - 1, limit);
        Specification<ProblemArchive> spec = ProblemArchiveSpecification.hasLanguage(language)
                .and(ProblemArchiveSpecification.hasDifficulty(difficulty))
                .and(ProblemArchiveSpecification.titleContains(search));
        final var page = problemArchiveRepository.findAll(spec, pageable);
        return new Pageable<>(page.get().map(p -> {
            try {
                return fromProblemArchive(p);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList(), pageNo, page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ProblemArchiveDto getProblemBySlug(String slug) throws JsonProcessingException {
        ProblemArchive problem = problemArchiveRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Problem not found: " + slug));
        return fromProblemArchive(problem);
    }

    @CacheEvict(value = CacheConfig.PROBLEMS_CACHE, allEntries = true)
    public ProblemArchive createProblem(ProblemInfo problem, MultipartFile file,String filePath) throws Exception {
        if(file!=null){
            final var filee=utilityService.multipartToFile(file,file.getOriginalFilename());
            try{
                final var por=s3StorageService.uploadFile(filee,file.getOriginalFilename());
            } finally {
                if(filee.delete()){
                    log.info("Deleted temporary file");
                }else {
                    log.warn("File deletion failed");
                }
            }
            filePath = s3StorageService.uploadedFilePath(file.getOriginalFilename());; // Construct the file's URL
        }
        if(filePath==null) throw new Exception("No Filepath found");
        final var exProbelm=problemArchiveRepository.findByTitle(problem.getTitle());
        if(exProbelm.isPresent()) return exProbelm.get();
        final var newProblem=ProblemArchive
                .builder()
                .slug(toSlug(problem.getSlug() != null ? problem.getSlug() : problem.getTitle()))
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

    @Cacheable(value = CacheConfig.PROBLEM_TAGS_CACHE, key = "#pageNo + '-' + #pageSize")
    public Pageable<ProblemTagDto> getProblemTags(int pageNo, int pageSize) {
        PageRequest pageable = PageRequest.of(pageNo - 1, pageSize); // Page index is 0-based in Spring Data
        final var page=problemTagRepository.findAll(pageable);
        return new Pageable<>(
                page.get().map((tag)->new ProblemTagDto(tag.getId(),tag.getTitle(),tag.getDescription(),tag.getImageUrl())).toList(),
                pageNo,
                page.getTotalElements());
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
        }).toList(),pageNo,page.getTotalElements());
    }

    public Pageable<ProblemArchiveDto> getTagsProblems(List<String> tags, int pageNo, int pageSize) {
        if(tags==null || tags.isEmpty()) return Pageable.emptyPage();
        PageRequest pageable = PageRequest.of(pageNo - 1, pageSize); // Page index is 0-based in Spring Data
        final var page=problemTagAssociationRepository.findAllByTagIdIn(tags,pageable);
        final var problemIds=page.stream().map(ProblemTagAssociation::getProblemId).toList();
        final var problems=problemArchiveRepository.findAllById(problemIds);

        return new Pageable<>(problems.stream().map((problem)-> {
            try {
                return fromProblemArchive(problem);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList(),pageNo,page.getTotalElements());
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
        if (!problemTagRepository.existsById(tagId) || !problemArchiveRepository.existsById(problemId)) {
            throw new Exception("invalid tagId/problemId");
        }
        // Return existing association if already linked (idempotent)
        if (problemTagAssociationRepository.existsByTagIdAndProblemId(tagId, problemId)) {
            return new ProblemTagAssociationDto(
                    ProblemTagAssociation.builder().tagId(tagId).problemId(problemId).build());
        }
        final var association = ProblemTagAssociation.builder().tagId(tagId).problemId(problemId).build();
        return new ProblemTagAssociationDto(problemTagAssociationRepository.save(association));
    }

    @CacheEvict(value = CacheConfig.PROBLEMS_CACHE, allEntries = true)
    public List<ProblemArchiveDto> createProblems(List<ProblemInfoWithPath> problems) throws JsonProcessingException {
        final List<ProblemArchive> problemArchives=new ArrayList<>();
        for(final ProblemInfoWithPath problemInfoWithPath : problems){
            final var arcive=ProblemArchive.builder()
                            .slug(toSlug(problemInfoWithPath.getSlug() != null
                                    ? problemInfoWithPath.getSlug()
                                    : problemInfoWithPath.getTitle()))
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
        String rawImages = problemArchive.getProblemImages();
        List<ProblemImage> problemImages;
        if (rawImages == null || rawImages.isBlank() || rawImages.equals("null")) {
            problemImages = Collections.emptyList();
        } else {
            problemImages = objectMapper.readValue(rawImages, new TypeReference<List<ProblemImage>>() {});
            if (problemImages == null) problemImages = Collections.emptyList();
        }

        List<ProblemPlatform> platforms;
        try {
            platforms = problemArchive.getPlatforms();
        } catch (Exception e) {
            platforms = Collections.emptyList();
        }

        return ProblemArchiveDto.builder()
                .id(problemArchive.getId())
                .slug(problemArchive.getSlug())
                .title(problemArchive.getTitle())
                .description(problemArchive.getDescription())
                .language(problemArchive.getLanguage())
                .difficulty(problemArchive.getDifficulty())
                .platforms(platforms)
                .filePath(problemArchive.getFilePath())
                .problemImages(problemImages)
                .build();
    }

    /** Converts a title or explicit slug to a URL-safe kebab-case slug. */
    private String toSlug(String input) {
        return input.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }

    @Transactional(readOnly = true)
    public String getProblemPresignedUrl(String slug) {
        ProblemArchive problem = problemArchiveRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Problem not found: " + slug));
        if (problem.getFilePath() == null || problem.getFilePath().isBlank()) {
            throw new RuntimeException("No file attached to problem: " + slug);
        }
        return s3StorageService.getPresignedUrl(problem.getFilePath());
    }

    @Transactional(readOnly = true)
    public ProblemArchiveDto getProblemById(String id) throws JsonProcessingException {
        ProblemArchive problem = problemArchiveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found: " + id));
        return fromProblemArchive(problem);
    }

    @CacheEvict(value = {CacheConfig.PROBLEMS_CACHE}, allEntries = true)
    @Transactional
    public ProblemArchiveDto updateProblem(String id, UpdateProblemDto dto) throws JsonProcessingException {
        ProblemArchive problem = problemArchiveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Problem not found: " + id));
        if (dto.getTitle() != null) problem.setTitle(dto.getTitle());
        if (dto.getSlug() != null) problem.setSlug(toSlug(dto.getSlug()));
        if (dto.getDescription() != null) problem.setDescription(dto.getDescription());
        if (dto.getLanguage() != null) problem.setLanguage(dto.getLanguage());
        if (dto.getDifficulty() != null) problem.setDifficulty(dto.getDifficulty());
        if (dto.getPlatforms() != null) problem.setPlatforms(dto.getPlatforms());
        if (dto.getFilePath() != null) problem.setFilePath(dto.getFilePath());
        if (dto.getProblemImages() != null)
            problem.setProblemImages(objectMapper.writeValueAsString(dto.getProblemImages()));
        return fromProblemArchive(problemArchiveRepository.save(problem));
    }

    @CacheEvict(value = {CacheConfig.PROBLEMS_CACHE}, allEntries = true)
    @Transactional
    public void deleteProblem(String id) {
        if (!problemArchiveRepository.existsById(id))
            throw new RuntimeException("Problem not found: " + id);
        problemTagAssociationRepository.deleteAllByProblemId(id);
        problemArchiveRepository.deleteById(id);
    }

    @CacheEvict(value = CacheConfig.PROBLEM_TAGS_CACHE, allEntries = true)
    @Transactional
    public ProblemTagDto updateTag(String id, UpdateTagDto dto) {
        ProblemTag tag = problemTagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found: " + id));
        if (dto.getTitle() != null) tag.setTitle(dto.getTitle());
        if (dto.getDescription() != null) tag.setDescription(dto.getDescription());
        if (dto.getImageUrl() != null) tag.setImageUrl(dto.getImageUrl());
        return new ProblemTagDto(problemTagRepository.save(tag));
    }

    @CacheEvict(value = CacheConfig.PROBLEM_TAGS_CACHE, allEntries = true)
    @Transactional
    public void deleteTag(String id) {
        if (!problemTagRepository.existsById(id))
            throw new RuntimeException("Tag not found: " + id);
        problemTagAssociationRepository.deleteAllByTagId(id);
        problemTagRepository.deleteById(id);
    }

    public void removeProblemFromTag(String tagId, String problemId) {
        problemTagAssociationRepository.deleteByTagIdAndProblemId(tagId, problemId);
    }

    @Transactional
    public List<ProblemTagAssociationDto> addAllProblemsToTag(String tagId, List<String> problemIds) throws Exception {
        if (!problemTagRepository.existsById(tagId)) {
            throw new Exception("invalid tagId");
        }

        // Group check: one query for all IDs instead of N separate existsById calls
        final Set<String> foundIds = problemArchiveRepository.findAllById(problemIds)
                .stream()
                .map(ProblemArchive::getId)
                .collect(Collectors.toSet());

        final var missingIds = problemIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!missingIds.isEmpty()) {
            throw new Exception("invalid problemId(s): " + missingIds);
        }

        // Collect only new associations (idempotent)
        final List<ProblemTagAssociation> toSave = problemIds.stream()
                .filter(id -> !problemTagAssociationRepository.existsByTagIdAndProblemId(tagId, id))
                .map(id -> ProblemTagAssociation.builder().tagId(tagId).problemId(id).build())
                .toList();

        final var savedAssociations = problemTagAssociationRepository.saveAll(toSave);
        return savedAssociations.stream().map(ProblemTagAssociationDto::new).toList();
    }
}