package com.vi5hnu.codesprout.repository;

import com.vi5hnu.codesprout.entity.ProblemArchive;
import com.vi5hnu.codesprout.entity.ProblemTag;
import com.vi5hnu.codesprout.entity.ProblemTagAssociation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemTagAssociationRepository extends JpaRepository<ProblemTagAssociation, String>{
    Page<ProblemTagAssociation> findAllByTagId(String tagId, PageRequest pageRequest);
}