package com.vi5hnu.codesprout.events.authEvents;

import com.vi5hnu.codesprout.entity.user.UserModel;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PasswordUpdateComplete extends ApplicationEvent {
    private final UserModel userModel;

    public PasswordUpdateComplete(UserModel userModel) {
        super(userModel);
        this.userModel=userModel;
    }
}