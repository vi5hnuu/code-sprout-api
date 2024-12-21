package com.vi5hnu.codesprout.repository;

import com.vi5hnu.codesprout.entity.ProblemArchive;
import com.vi5hnu.codesprout.specifications.ProblemArchiveSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProblemArchiveRepository extends JpaRepository<ProblemArchive, String>  , JpaSpecificationExecutor<ProblemArchive> {
    boolean existsByTitle(String title);
    Optional<ProblemArchive> findByTitle(String title);
}