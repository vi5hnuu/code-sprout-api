package com.vi5hnu.codesprout.Dto;

import com.vi5hnu.codesprout.commons.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RegisterRequestDto {
    @NotBlank(message = "first name cannot be blank") private String firstName;
    @NotBlank(message = "last name cannot be blank") private String lastName;
    @NotBlank(message = "user name cannot be blank") private String userName;
    @Pattern(regexp = Constants.EMAIL_PATTERN,message = "invalid email id, ex : example@gmail.com") private String email;
    @NotBlank(message = "password cannot be blank") private String password;
}
