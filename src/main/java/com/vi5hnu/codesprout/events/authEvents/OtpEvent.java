package com.vi5hnu.codesprout.events.authEvents;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;


@Getter
public class OtpEvent extends ApplicationEvent {
    private final String userId;
    private final String name;
    private final String email;
    private final String otp;
    public OtpEvent(String userId,String name,String email,String otp) {
        super(name);
        this.userId=userId;
        this.otp=otp;
        this.name=name;
        this.email=email;
    }
}
