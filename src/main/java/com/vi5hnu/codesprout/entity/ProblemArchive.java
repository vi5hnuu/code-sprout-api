package com.vi5hnu.codesprout.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vi5hnu.codesprout.enums.ProblemDifficulty;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import com.vi5hnu.codesprout.models.ProblemPlatform;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = ProblemArchive.TABLE_NAME)
public class ProblemArchive extends BaseDomain {
    public static final String PREFIX     = "PID";
    public static final String TABLE_NAME = "problem_archive";

    /** URL-safe identifier used by the frontend for routing (e.g. "two-sum"). */
    @Column(unique = true, nullable = false)
    private String slug;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProblemLanguage language;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProblemDifficulty difficulty;

    private String platforms;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    private String problemImages;

    @PrePersist
    public void prePersist() {
        if (platforms == null)     platforms = "[]";
        if (problemImages == null) problemImages = "[]";
        initId(PREFIX);
        // Auto-derive slug from title if not set explicitly
        if (this.slug == null && this.title != null) {
            this.slug = title.trim().toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .replaceAll("\\s+", "-")
                    .replaceAll("-+", "-");
        }
    }

    public List<ProblemPlatform> getPlatforms() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(this.platforms, new TypeReference<>() {});
    }
}
