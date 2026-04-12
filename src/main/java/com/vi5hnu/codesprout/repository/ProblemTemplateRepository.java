package com.vi5hnu.codesprout.repository;

import com.vi5hnu.codesprout.entity.ProblemTemplate;
import com.vi5hnu.codesprout.enums.ProblemLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProblemTemplateRepository extends JpaRepository<ProblemTemplate, String> {
    List<ProblemTemplate> findByProblemId(String problemId);
    Optional<ProblemTemplate> findByProblemIdAndLanguage(String problemId, ProblemLanguage language);
}
