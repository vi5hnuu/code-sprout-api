package com.vi5hnu.codesprout.annotation;

import com.vi5hnu.codesprout.models.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireUserWith {
    boolean isEnabled() default true;   // Check if the user is enabled
    boolean isDeleted() default false;  // Check if the user is deleted
    boolean isLocked() default false;   // Check if the user is locked
    UserRole[] hasRoles() default {};  // Check if the user has role
}
