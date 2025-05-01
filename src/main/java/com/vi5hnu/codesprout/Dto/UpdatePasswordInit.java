package com.vi5hnu.codesprout.Dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdatePasswordInit {
    @NotBlank(message = "username/email cannot be blank") private String usernameEmail;
}
