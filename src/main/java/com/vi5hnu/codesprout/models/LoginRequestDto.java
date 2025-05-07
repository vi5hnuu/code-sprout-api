package com.vi5hnu.codesprout.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
    @NotBlank(message = "either email or username should be provided.") private String usernameEmail;
    @NotBlank(message = "password cannot be blank.") private String password;
}
