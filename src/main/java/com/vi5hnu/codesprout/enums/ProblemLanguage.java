package com.vi5hnu.codesprout.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ProblemLanguage {
    CPP("cpp");
    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ProblemLanguage fromValue(String value) {
        for (ProblemLanguage language : ProblemLanguage.values()) {
            if (language.value.equalsIgnoreCase(value)) { // Case insensitive check
                return language;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);
    }
}
