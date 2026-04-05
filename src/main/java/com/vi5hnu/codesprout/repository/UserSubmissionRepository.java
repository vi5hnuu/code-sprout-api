package com.vi5hnu.codesprout.repository;

import com.vi5hnu.codesprout.entity.UserSubmission;
import com.vi5hnu.codesprout.enums.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserSubmissionRepository extends JpaRepository<UserSubmission, String> {

    Page<UserSubmission> findByUserIdOrderBySubmittedAtDesc(String userId, Pageable pageable);

    Page<UserSubmission> findByUserIdAndProblemIdOrderBySubmittedAtDesc(String userId, String problemId, Pageable pageable);

    /** Returns distinct problem IDs where the user has at least one ACCEPTED official submission. */
    @Query("SELECT DISTINCT s.problemId FROM UserSubmission s WHERE s.userId = :userId AND s.status = :status AND s.isOfficial = true")
    List<String> findAcceptedProblemIds(@Param("userId") String userId, @Param("status") SubmissionStatus status);

    boolean existsByUserIdAndProblemIdAndStatus(String userId, String problemId, SubmissionStatus status);
}
