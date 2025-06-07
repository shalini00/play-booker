package com.app.playbooker.controller;

import com.app.playbooker.dto.OtpCreationDTO;
import com.app.playbooker.dto.OtpVerificationDTO;
import com.app.playbooker.entity.OtpVerification;
import com.app.playbooker.entity.User;
import com.app.playbooker.enums.OtpType;
import com.app.playbooker.repository.OtpVerificationRepository;
import com.app.playbooker.repository.UserRepository;
import com.app.playbooker.service.OtpService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/otp")
@Log4j2
public class OtpController {

    @Autowired
    private OtpVerificationRepository otpVerificationRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationDTO otpVerificationDTO) {
        Optional<OtpVerification> optionalOtp = otpVerificationRepository
                .findFirstByOtpAndVerifiedFalseAndExpiryTimeAfter(otpVerificationDTO.getOtp(), LocalDateTime.now());

        OtpVerification otp = optionalOtp.orElseThrow(() -> new RuntimeException("OTP not found"));

        if (otp.isVerified()) {
            throw new RuntimeException("OTP you entered is already verified. Please regenerate and verify again.");
        }

        if (!otp.getOtp().equals(otpVerificationDTO.getOtp()) || otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }

        otp.setVerified(true);
        User user = null;
        if (OtpType.EMAIL.equals(otpVerificationDTO.getOtpType())) {
            user = userRepository.findByEmail(otp.getEmail());
            user.setEmailVerified(true);
        } else {
            user = userRepository.findByPhoneNumber(otp.getPhoneNumber());
            user.setPhoneNumberVerified(true);
        }
        userRepository.save(user);
        otpVerificationRepository.save(otp);

        return ResponseEntity.ok("OTP verified");
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateOtp(@RequestBody OtpCreationDTO otpCreationDTO) {
        String email = otpCreationDTO.getEmail();
        String phone = otpCreationDTO.getPhoneNumber();
        OtpType otpType = otpCreationDTO.getOtpType();
        if (OtpType.EMAIL.equals(otpType) && !StringUtils.hasText(email)) {
            throw new RuntimeException("Email is not present");
        }
        if (OtpType.PHONE.equals(otpType) && !StringUtils.hasText(phone)) {
            throw new RuntimeException("Phone Number is not present");
        }
        User user = otpType == OtpType.EMAIL ? userRepository.findByEmail(email) : userRepository.findByPhoneNumber(phone);
        otpService.generateAndSendOtp(user, otpType);
        return new ResponseEntity<>("OTP generated.", HttpStatus.CREATED);
    }
}
