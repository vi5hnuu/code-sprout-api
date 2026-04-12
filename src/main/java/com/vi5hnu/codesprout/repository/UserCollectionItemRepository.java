package com.vi5hnu.codesprout.repository;

import com.vi5hnu.codesprout.entity.UserCollectionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserCollectionItemRepository extends JpaRepository<UserCollectionItem, String> {
    List<UserCollectionItem> findByCollectionIdOrderByAddedAtDesc(String collectionId);
    Optional<UserCollectionItem> findByCollectionIdAndProblemId(String collectionId, String problemId);
    void deleteByCollectionIdAndProblemId(String collectionId, String problemId);
    long countByCollectionId(String collectionId);

    @Query("SELECT ci.collectionId FROM UserCollectionItem ci WHERE ci.problemId = :problemId AND ci.collectionId IN :collectionIds")
    List<String> findCollectionIdsContainingProblem(@Param("problemId") String problemId, @Param("collectionIds") List<String> collectionIds);
}
