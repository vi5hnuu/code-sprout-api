package com.vi5hnu.codesprout.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vi5hnu.codesprout.entity.ProblemArchive;
import com.vi5hnu.codesprout.entity.UserBookmark;
import com.vi5hnu.codesprout.enums.BookmarkType;
import com.vi5hnu.codesprout.models.ProblemArchiveDto;
import com.vi5hnu.codesprout.models.ProblemImage;
import com.vi5hnu.codesprout.models.ProblemPlatform;
import com.vi5hnu.codesprout.repository.ProblemArchiveRepository;
import com.vi5hnu.codesprout.repository.UserBookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final UserBookmarkRepository bookmarkRepository;
    private final ProblemArchiveRepository problemRepository;
    private final ObjectMapper objectMapper;

    // ── Generic toggle ────────────────────────────────────────────────────────

    /**
     * Toggles a bookmark for any supported type.
     * Returns {@code true} if the item is now bookmarked, {@code false} if removed.
     */
    @Transactional
    public boolean toggle(String userId, BookmarkType type, String targetId) {
        var existing = bookmarkRepository.findByUserIdAndTypeAndTargetId(userId, type, targetId);
        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            return false;
        }
        bookmarkRepository.save(
            UserBookmark.builder()
                .userId(userId)
                .type(type)
                .targetId(targetId)
                .build()
        );
        return true;
    }

    // ── Type-specific fetch methods ───────────────────────────────────────────

    /**
     * Returns all bookmarked problems for a user, ordered by most recently bookmarked.
     */
    @Transactional(readOnly = true)
    public List<ProblemArchiveDto> getMyProblemBookmarks(String userId) {
        List<String> ids = bookmarkRepository.findTargetIdsByUserIdAndType(userId, BookmarkType.PROBLEM);
        if (ids.isEmpty()) return List.of();

        var problems = problemRepository.findAllById(ids);

        // Preserve bookmark order (most recent first)
        Map<String, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) orderMap.put(ids.get(i), i);
        problems.sort(Comparator.comparingInt(p -> orderMap.getOrDefault(p.getId(), Integer.MAX_VALUE)));

        return problems.stream().map(this::toProblemDto).toList();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private ProblemArchiveDto toProblemDto(ProblemArchive p) {
        List<ProblemImage> images;
        try {
            String raw = p.getProblemImages();
            images = (raw == null || raw.isBlank() || raw.equals("null"))
                ? Collections.emptyList()
                : objectMapper.readValue(raw, new TypeReference<>() {});
            if (images == null) images = Collections.emptyList();
        } catch (Exception e) {
            images = Collections.emptyList();
        }

        List<ProblemPlatform> platforms;
        try {
            platforms = p.getPlatforms();
        } catch (Exception e) {
            platforms = Collections.emptyList();
        }

        return ProblemArchiveDto.builder()
                .id(p.getId())
                .slug(p.getSlug())
                .title(p.getTitle())
                .description(p.getDescription())
                .language(p.getLanguage())
                .difficulty(p.getDifficulty())
                .filePath(p.getFilePath())
                .platforms(platforms)
                .problemImages(images)
                .build();
    }
}
