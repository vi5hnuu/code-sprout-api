package com.vi5hnu.codesprout.entity;

import com.vi5hnu.codesprout.enums.ProblemCategory;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import com.vi5hnu.codesprout.models.ProblemPlatform;
import jakarta.persistence.*;
import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity()
@Table(name = ProblemArchive.TABLE_NAME)
public class ProblemArchive {
    public final static String PREFIX = "PID";
    public final static String TABLE_NAME = "problem_archive";

    @Id
    private String id;
    private String title;
    private String description;
    private ProblemLanguage language;
    private ProblemCategory category;
    private List<ProblemPlatform> platforms;

    @Column(name = "file_path",nullable = false)
    private String filePath;

    @PrePersist
    public void assignId() {
        if(platforms==null) platforms = Collections.emptyList();
        if (this.id == null) this.id = (PREFIX + UUID.randomUUID().toString().replace("_","")).substring(0,32);
    }
}