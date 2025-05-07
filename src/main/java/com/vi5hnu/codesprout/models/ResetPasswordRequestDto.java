package com.vi5hnu.codesprout.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequestDto {
    @NotBlank(message = "username/email is required") private String usernameEmail;//autofill this in frontend with username or email used for forgot-password
    @NotBlank(message = "otp is required") private String otp;
    @NotBlank(message = "password is required") private String password;
    @NotBlank(message = "confirm password is required") private String confirmPassword;
}
