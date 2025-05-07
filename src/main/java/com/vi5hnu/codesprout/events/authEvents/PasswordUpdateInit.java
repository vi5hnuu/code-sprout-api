package com.vi5hnu.codesprout.events.authEvents;

import com.vi5hnu.codesprout.models.UserDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PasswordUpdateInit extends ApplicationEvent {
    private final UserDto user;
    private final String otp;

    public PasswordUpdateInit(UserDto user,String otp) {
        super(user);
        this.user=user;
        this.otp=otp;
    }
}