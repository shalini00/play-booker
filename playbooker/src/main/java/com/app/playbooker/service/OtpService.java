package com.app.playbooker.service;

import com.app.playbooker.entity.OtpVerification;
import com.app.playbooker.entity.User;
import com.app.playbooker.enums.OtpType;
import com.app.playbooker.exceptions.OtpException;
import com.app.playbooker.notification.NotificationFactory;
import com.app.playbooker.repository.OtpVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import static com.app.playbooker.service.BookingService.EMAIL;
import static com.app.playbooker.service.BookingService.WHATSAPP;

@Service
public class OtpService {

    @Autowired
    private OtpVerificationRepository otpVerificationRepository;

    @Autowired
    private NotificationFactory notificationFactory;

    @Transactional
    public void generateAndSendOtp(User user, OtpType otpType) {
        SecureRandom random = new SecureRandom();
        String otp = String.valueOf(100000 + random.nextInt(900000));  // Generate 6 digit otp
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);
        saveOtp(user, otpType, otp, expiry);
        if (OtpType.EMAIL.equals(otpType)) {
            notificationFactory.getNotificationServiceByType(EMAIL).sendOTP(user, otp);
        } else {
            notificationFactory.getNotificationServiceByType(WHATSAPP).sendOTP(user, otp);
        }
    }

    private void saveOtp(User user, OtpType otpType, String otp, LocalDateTime expiry) {
        try {
            OtpVerification otpEntry = new OtpVerification();
            if (OtpType.EMAIL.equals(otpType)) {
                otpEntry.setEmail(user.getEmail());
            } else {
                otpEntry.setPhoneNumber(user.getPhoneNumber());
            }
            otpEntry.setOtp(otp);
            otpEntry.setExpiryTime(expiry);
            otpEntry.setVerified(false);
            otpVerificationRepository.save(otpEntry);
        } catch (Exception e) {
            throw new OtpException("Error occurred while saving OTP due to " + e.getMessage());
        }

    }
}
