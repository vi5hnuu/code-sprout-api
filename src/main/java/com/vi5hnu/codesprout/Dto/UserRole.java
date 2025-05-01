package com.vi5hnu.codesprout.Dto;

import lombok.Getter;

@Getter
public enum UserRole {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER");

    UserRole(String name){
        this.name=name;
    }
    private final String name;

    public static UserRole[] getAll(){
        return UserRole.values();
    }
}