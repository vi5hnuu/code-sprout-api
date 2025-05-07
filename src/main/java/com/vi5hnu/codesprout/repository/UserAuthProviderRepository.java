package com.vi5hnu.codesprout.repository;

import com.vi5hnu.codesprout.commons.Constants;
import com.vi5hnu.codesprout.entity.user.UserAuthProviderModel;
import com.vi5hnu.codesprout.entity.user.UserModel;
import com.vi5hnu.codesprout.enums.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserAuthProviderRepository extends JpaRepository<UserAuthProviderModel,String> {
    List<UserAuthProviderModel> findByUserId(String userId);
    Boolean existsByUserId(String userId);
    Optional<UserAuthProviderModel> findByUserIdAndAccountType(String userId, AccountType accountType);
}
