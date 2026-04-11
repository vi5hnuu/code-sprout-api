package com.vi5hnu.codesprout.repository;

import com.vi5hnu.codesprout.entity.ProblemTagAssociation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProblemTagAssociationRepository extends JpaRepository<ProblemTagAssociation, String> {
    List<ProblemTagAssociation> findAllByProblemId(String problemId);
    Page<ProblemTagAssociation> findAllByTagId(String tagId, PageRequest pageRequest);
    Page<ProblemTagAssociation> findAllByTagIdIn(List<String> tagIds, PageRequest pageRequest);
    boolean existsByTagIdAndProblemId(String tagId, String problemId);

    @Transactional
    void deleteByTagIdAndProblemId(String tagId, String problemId);

    @Transactional
    void deleteAllByTagId(String tagId);

    @Transactional
    void deleteAllByProblemId(String problemId);
}
