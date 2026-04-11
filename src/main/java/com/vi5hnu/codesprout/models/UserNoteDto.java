package com.vi5hnu.codesprout.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vi5hnu.codesprout.entity.UserNote;
import lombok.*;

import java.time.Instant;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserNoteDto {
    private String id;
    private String problemId;
    private String content;
    private Instant updatedAt;

    public static UserNoteDto from(UserNote note) {
        return UserNoteDto.builder()
                .id(note.getId())
                .problemId(note.getProblemId())
                .content(note.getContent())
                .updatedAt(note.getUpdatedAt())
                .build();
    }
}
