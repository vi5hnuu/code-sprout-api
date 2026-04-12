package com.vi5hnu.codesprout.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vi5hnu.codesprout.entity.ProblemHint;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProblemHintDto {
    private String id;
    private int hintOrder;
    private String content;

    public static ProblemHintDto from(ProblemHint hint) {
        return ProblemHintDto.builder()
                .id(hint.getId())
                .hintOrder(hint.getHintOrder())
                .content(hint.getContent())
                .build();
    }
}
