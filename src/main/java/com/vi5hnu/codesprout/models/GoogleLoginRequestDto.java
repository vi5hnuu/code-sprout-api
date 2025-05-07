package com.vi5hnu.codesprout.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleLoginRequestDto {
    //picture,picture ? true = picture,email,name (full name)
    @NotBlank(message = "invalid token.")private String idToken;//jwt
}
