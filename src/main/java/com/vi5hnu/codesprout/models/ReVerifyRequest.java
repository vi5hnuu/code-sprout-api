package com.vi5hnu.codesprout.models;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ReVerifyRequest {
    @NotBlank(message = "username/email cannot be blank.") private String usernameEmail;
}
