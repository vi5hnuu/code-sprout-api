package com.vi5hnu.codesprout.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vi5hnu.codesprout.entity.ProblemArchive;
import com.vi5hnu.codesprout.entity.UserCollection;
import com.vi5hnu.codesprout.entity.UserCollectionItem;
import com.vi5hnu.codesprout.exceptions.ApiException;
import com.vi5hnu.codesprout.models.*;
import com.vi5hnu.codesprout.repository.ProblemArchiveRepository;
import com.vi5hnu.codesprout.repository.UserCollectionItemRepository;
import com.vi5hnu.codesprout.repository.UserCollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final UserCollectionRepository collectionRepo;
    private final UserCollectionItemRepository itemRepo;
    private final ProblemArchiveRepository problemRepo;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<UserCollectionDto> getMyCollections(String userId) {
        return collectionRepo.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(c -> UserCollectionDto.from(c, itemRepo.countByCollectionId(c.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public UserCollectionDto getDetail(String userId, String collectionId) throws ApiException {
        UserCollection col = collectionRepo.findByIdAndUserId(collectionId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Collection not found"));

        List<String> problemIds = itemRepo.findByCollectionIdOrderByAddedAtDesc(collectionId)
                .stream().map(UserCollectionItem::getProblemId).toList();

        List<ProblemArchive> problems = problemRepo.findAllById(problemIds);
        Map<String, Integer> order = new HashMap<>();
        for (int i = 0; i < problemIds.size(); i++) order.put(problemIds.get(i), i);
        problems.sort(Comparator.comparingInt(p -> order.getOrDefault(p.getId(), Integer.MAX_VALUE)));

        UserCollectionDto dto = UserCollectionDto.from(col, problems.size());
        dto.setProblems(problems.stream().map(p -> toProblemDto(p)).toList());
        return dto;
    }

    @Transactional
    public UserCollectionDto create(String userId, String name, String description, boolean isPublic) {
        UserCollection col = UserCollection.builder()
                .userId(userId)
                .name(name)
                .description(description)
                .isPublic(isPublic)
                .build();
        return UserCollectionDto.from(collectionRepo.save(col), 0);
    }

    @Transactional
    public UserCollectionDto update(String userId, String collectionId, String name, String description, boolean isPublic) throws ApiException {
        UserCollection col = collectionRepo.findByIdAndUserId(collectionId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Collection not found"));
        col.setName(name);
        col.setDescription(description);
        col.setPublic(isPublic);
        long count = itemRepo.countByCollectionId(collectionId);
        return UserCollectionDto.from(collectionRepo.save(col), count);
    }

    @Transactional
    public void delete(String userId, String collectionId) throws ApiException {
        UserCollection col = collectionRepo.findByIdAndUserId(collectionId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Collection not found"));
        collectionRepo.delete(col);
    }

    @Transactional
    public boolean toggleItem(String userId, String collectionId, String problemId) throws ApiException {
        collectionRepo.findByIdAndUserId(collectionId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Collection not found"));
        Optional<UserCollectionItem> existing = itemRepo.findByCollectionIdAndProblemId(collectionId, problemId);
        if (existing.isPresent()) {
            itemRepo.delete(existing.get());
            return false;
        }
        itemRepo.save(UserCollectionItem.builder()
                .collectionId(collectionId)
                .problemId(problemId)
                .build());
        return true;
    }

    @Transactional(readOnly = true)
    public List<String> getCollectionIdsContaining(String userId, String problemId) {
        List<String> myIds = collectionRepo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(UserCollection::getId).toList();
        if (myIds.isEmpty()) return List.of();
        return itemRepo.findCollectionIdsContainingProblem(problemId, myIds);
    }

    private ProblemArchiveDto toProblemDto(ProblemArchive p) {
        List<ProblemImage> images;
        try {
            String raw = p.getProblemImages();
            images = (raw == null || raw.isBlank()) ? Collections.emptyList()
                    : objectMapper.readValue(raw, new TypeReference<>() {});
        } catch (Exception e) { images = Collections.emptyList(); }

        List<ProblemPlatform> platforms;
        try { platforms = p.getPlatforms(); }
        catch (Exception e) { platforms = Collections.emptyList(); }

        return ProblemArchiveDto.builder()
                .id(p.getId()).slug(p.getSlug()).title(p.getTitle())
                .description(p.getDescription()).language(p.getLanguage())
                .difficulty(p.getDifficulty()).filePath(p.getFilePath())
                .platforms(platforms).problemImages(images).build();
    }
}
