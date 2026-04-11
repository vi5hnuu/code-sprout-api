package com.vi5hnu.codesprout.repository;

import com.vi5hnu.codesprout.entity.UserNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserNoteRepository extends JpaRepository<UserNote, String> {
    Optional<UserNote> findByUserIdAndProblemId(String userId, String problemId);
    void deleteByUserIdAndProblemId(String userId, String problemId);
}
