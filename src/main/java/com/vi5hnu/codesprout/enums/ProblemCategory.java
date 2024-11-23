package com.vi5hnu.codesprout.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;



@AllArgsConstructor
public enum ProblemCategory {
    EASY("EASY"),
    MEDIUM("MEDIUM"),
    HARD("HARD");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ProblemCategory fromValue(String value) {
        return ProblemCategory.valueOf(ProblemCategory.class, value.toUpperCase());
    }
}
