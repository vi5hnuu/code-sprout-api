package com.vi5hnu.codesprout.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.vi5hnu.codesprout.entity.ProblemTag;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProblemTagDto {
    private String id;
    private String title;
    private String description;
    private String imageUrl;

    public ProblemTagDto(ProblemTag tag){
        this.setId(tag.getId());
        this.setTitle(tag.getTitle());
        this.setDescription(tag.getDescription());
        this.setImageUrl(tag.getImageUrl());
    }
}