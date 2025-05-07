package com.vi5hnu.codesprout.models;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class RoleDto {
    @NotBlank(message = "userId cannot be blank.") private String userId;
    @NonNull private UserRole role;
}
