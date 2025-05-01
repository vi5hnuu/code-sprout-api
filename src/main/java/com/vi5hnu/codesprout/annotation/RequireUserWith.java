package com.vi5hnu.codesprout.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireUserWith {
    boolean isEnabled() default true;   // Check if the user is enabled
    boolean isDeleted() default false;  // Check if the user is deleted
    boolean isLocked() default false;   // Check if the user is locked
}
