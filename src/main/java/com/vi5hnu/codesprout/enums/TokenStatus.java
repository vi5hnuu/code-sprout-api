package com.vi5hnu.codesprout.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TokenStatus {
    USED("USED"),
    REVOKED("REVOKED"),
    UN_USED("UN_USED");
    public final String status;
}
