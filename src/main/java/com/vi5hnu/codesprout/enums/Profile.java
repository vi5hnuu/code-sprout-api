package com.vi5hnu.codesprout.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Profile {
    DEFAULT("default"),
    PROD("prod"),
    DEV("dev");
    public final String profile;
}
