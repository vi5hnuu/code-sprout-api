package com.vi5hnu.codesprout.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ProblemCategory {
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ProblemCategory fromValue(String value) {
        for (ProblemCategory category : ProblemCategory.values()) {
            if (category.value.equalsIgnoreCase(value)) { // Case insensitive check
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);
    }
}
