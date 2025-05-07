package com.vi5hnu.codesprout.models;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdatePasswordDto {
    @NotBlank(message = "otp cannot be blank.") private String otp;
    @NotBlank(message = "old password cannot be blank.") private String oldPassword;
    @NotBlank(message = "new password cannot be blank") private String newPassword;
    @NotBlank(message = "confirm password cannot be blank") private String confirmPassword;
}
