package com.vi5hnu.codesprout.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum FileExtension {
    TXT("txt"),
    MD("md");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static FileExtension fromValue(String value) {
        return FileExtension.valueOf(FileExtension.class, value.toUpperCase());
    }
}
