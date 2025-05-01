package com.vi5hnu.codesprout.events.authEvents;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;


@Getter
public class AlertEvent extends ApplicationEvent {
    private final String name;
    private final String email;
    private final String alertMessage;
    public AlertEvent(String name, String email,String alertMessage) {
        super(name);
        this.name=name;
        this.email=email;
        this.alertMessage=alertMessage;
    }
}
