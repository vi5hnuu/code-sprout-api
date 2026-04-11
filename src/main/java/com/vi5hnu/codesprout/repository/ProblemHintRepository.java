package com.vi5hnu.codesprout.repository;

import com.vi5hnu.codesprout.entity.ProblemHint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemHintRepository extends JpaRepository<ProblemHint, String> {
    List<ProblemHint> findByProblemIdOrderByHintOrderAsc(String problemId);
    void deleteByProblemId(String problemId);
}
