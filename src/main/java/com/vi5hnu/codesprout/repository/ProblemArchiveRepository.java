package com.vi5hnu.codesprout.repository;

import com.vi5hnu.codesprout.entity.ProblemArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemArchiveRepository extends JpaRepository<ProblemArchive, String> {

}