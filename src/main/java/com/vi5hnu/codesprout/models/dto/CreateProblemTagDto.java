package com.vi5hnu.codesprout.models.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateProblemTagDto {
    @NotBlank(message = "title is required")
    private String title;
    private String description;

    @NotBlank(message = "tag image url is required")
    private String imageUrl;
}