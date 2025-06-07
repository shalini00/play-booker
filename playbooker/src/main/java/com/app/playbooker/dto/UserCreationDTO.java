package com.app.playbooker.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationDTO {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Username is mandatory")
    private String username;

    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Minimum password length is 8")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,}$", message = "Password should contain at least 1 uppercase character, lowercase character, special character, digit")
    private String password;

    @Pattern(regexp = "^$|^\\+?[0-9]{10,15}$", message = "Please enter a valid phone number")
    @Nullable
    private String phoneNumber;
}
