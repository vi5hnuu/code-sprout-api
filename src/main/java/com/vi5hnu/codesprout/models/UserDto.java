package com.vi5hnu.codesprout.models;

import com.vi5hnu.codesprout.enums.AccountType;
import lombok.*;

import java.sql.Timestamp;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserDto {
    private String id;
    private AccountType accountType;
    private String firstName;
    private String profileUrl;
    private String lastName;
    private String username;
    private String email;

    @Builder.Default()
    private boolean isLocked=false; // is suspended or not

    @Builder.Default()
    private boolean isEnabled=false; //is verified or not

    @Builder.Default()
    private boolean isDeleted=false;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp passwordUpdatedAt;
    private Set<UserRole> roles;
}