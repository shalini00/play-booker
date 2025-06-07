package com.app.playbooker.dto;

import com.app.playbooker.enums.OtpType;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OtpVerificationDTO {
    private String otp;
    private OtpType otpType;
}
