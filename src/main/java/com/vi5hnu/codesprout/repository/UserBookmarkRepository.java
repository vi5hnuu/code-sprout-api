package com.vi5hnu.codesprout.repository;

import com.vi5hnu.codesprout.entity.UserBookmark;
import com.vi5hnu.codesprout.enums.BookmarkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserBookmarkRepository extends JpaRepository<UserBookmark, String> {

    boolean existsByUserIdAndTypeAndTargetId(String userId, BookmarkType type, String targetId);

    Optional<UserBookmark> findByUserIdAndTypeAndTargetId(String userId, BookmarkType type, String targetId);

    @Query("SELECT b.targetId FROM UserBookmark b WHERE b.userId = :userId AND b.type = :type ORDER BY b.createdAt DESC")
    List<String> findTargetIdsByUserIdAndType(@Param("userId") String userId, @Param("type") BookmarkType type);
}
