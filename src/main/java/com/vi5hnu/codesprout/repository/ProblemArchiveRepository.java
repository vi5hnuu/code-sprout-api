package com.vi5hnu.codesprout.repository;

import com.vi5hnu.codesprout.entity.ProblemArchive;
import com.vi5hnu.codesprout.specifications.ProblemArchiveSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemArchiveRepository extends JpaRepository<ProblemArchive, String>  , JpaSpecificationExecutor<ProblemArchive> {
    boolean existsByTitle(String title);
    Optional<ProblemArchive> findByTitle(String title);

    @Query("SELECT pA FROM ProblemArchive pA WHERE pA.id IN :ids")
    List<ProblemArchive> findAllById(@Param("ids") List<String> ids);
}