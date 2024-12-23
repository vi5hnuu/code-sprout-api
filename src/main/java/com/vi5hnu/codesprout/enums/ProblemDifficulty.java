package com.vi5hnu.codesprout.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;



@AllArgsConstructor
public enum ProblemDifficulty {
    EASY("EASY"),
    MEDIUM("MEDIUM"),
    HARD("HARD");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ProblemDifficulty fromValue(String value) {
        return ProblemDifficulty.valueOf(ProblemDifficulty.class, value.toUpperCase());
    }
}
