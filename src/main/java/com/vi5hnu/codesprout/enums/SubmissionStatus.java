package com.vi5hnu.codesprout.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SubmissionStatus {
    ACCEPTED("ACCEPTED"),
    WRONG_ANSWER("WRONG_ANSWER"),
    TIME_LIMIT_EXCEEDED("TIME_LIMIT_EXCEEDED"),
    MEMORY_LIMIT_EXCEEDED("MEMORY_LIMIT_EXCEEDED"),
    RUNTIME_ERROR("RUNTIME_ERROR"),
    COMPILATION_ERROR("COMPILATION_ERROR"),
    PENDING("PENDING"),
    RUNNING("RUNNING");

    private final String value;

    @JsonValue
    public String getValue() { return value; }

    @JsonCreator
    public static SubmissionStatus fromValue(String value) {
        return SubmissionStatus.valueOf(SubmissionStatus.class, value.toUpperCase());
    }
}