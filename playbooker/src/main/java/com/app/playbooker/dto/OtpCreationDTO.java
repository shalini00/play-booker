package com.app.playbooker.dto;

import com.app.playbooker.enums.OtpType;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OtpCreationDTO {
    private String email;
    private String phoneNumber;
    private OtpType otpType;
}
