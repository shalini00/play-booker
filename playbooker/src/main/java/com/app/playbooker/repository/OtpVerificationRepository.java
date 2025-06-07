package com.app.playbooker.repository;

import com.app.playbooker.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, String> {
    OtpVerification findByOtp(String otp);
    Optional<OtpVerification> findFirstByOtpAndVerifiedFalseAndExpiryTimeAfter(String otp, LocalDateTime expiry);
}
