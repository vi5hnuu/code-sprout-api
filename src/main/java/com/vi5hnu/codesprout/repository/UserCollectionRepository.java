package com.vi5hnu.codesprout.repository;

import com.vi5hnu.codesprout.entity.UserCollection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCollectionRepository extends JpaRepository<UserCollection, String> {
    List<UserCollection> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<UserCollection> findByIdAndUserId(String id, String userId);
}
