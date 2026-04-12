package com.vi5hnu.codesprout.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vi5hnu.codesprout.entity.UserCollection;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserCollectionDto {
    private String id;
    private String name;
    private String description;
    private boolean isPublic;
    private long problemCount;
    private Instant createdAt;
    // populated only for detail view
    private List<ProblemArchiveDto> problems;

    public static UserCollectionDto from(UserCollection col, long count) {
        return UserCollectionDto.builder()
                .id(col.getId())
                .name(col.getName())
                .description(col.getDescription())
                .isPublic(col.isPublic())
                .problemCount(count)
                .createdAt(col.getCreatedAt())
                .build();
    }
}
