package com.vi5hnu.codesprout.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ProblemLanguage {
    CPP("CPP"),
    SQL("SQL");
    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ProblemLanguage fromValue(String value) {
        return ProblemLanguage.valueOf(ProblemLanguage.class, value.toUpperCase());
    }
}
