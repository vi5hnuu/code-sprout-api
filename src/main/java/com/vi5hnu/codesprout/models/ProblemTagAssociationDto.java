package com.vi5hnu.codesprout.models;

import com.vi5hnu.codesprout.entity.ProblemTagAssociation;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProblemTagAssociationDto {
    private String id;
    private String tagId;
    private String problemId;

    public ProblemTagAssociationDto(ProblemTagAssociation association){
        this.setId(association.getId());
        this.setTagId(association.getTagId());
        this.setProblemId(association.getProblemId());
    }
}