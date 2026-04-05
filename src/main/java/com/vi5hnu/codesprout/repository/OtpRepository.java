package com.vi5hnu.codesprout.repository;

import com.vi5hnu.codesprout.entity.user.OtpModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpModel,String>, JpaSpecificationExecutor<OtpModel> {
    Optional<OtpModel> findByOtp(String otp);
    Optional<OtpModel> findByUserId(String otp);
    List<OtpModel> findAllByUserId(String otp);
    Optional<OtpModel> findByOtpAndUserId(String otp,String userId);
}
