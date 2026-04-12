package com.vi5hnu.codesprout.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vi5hnu.codesprout.entity.ProblemTemplate;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProblemTemplateDto {
    private String id;
    private ProblemLanguage language;
    private String templateCode;

    public static ProblemTemplateDto from(ProblemTemplate t) {
        return ProblemTemplateDto.builder()
                .id(t.getId())
                .language(t.getLanguage())
                .templateCode(t.getTemplateCode())
                .build();
    }
}
