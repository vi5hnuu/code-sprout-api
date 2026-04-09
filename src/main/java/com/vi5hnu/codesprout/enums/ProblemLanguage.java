package com.vi5hnu.codesprout.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Supported problem languages.
 *
 * Each constant carries:
 * <ul>
 *   <li>{@code value}           — JSON/DB representation (e.g. "CPP").</li>
 *   <li>{@code judgeLanguageId} — identifier sent to the judge service (e.g. "cpp").</li>
 * </ul>
 *
 * Adding a new language: add a constant here — no other service needs a language map.
 */
@Getter
@AllArgsConstructor
public enum ProblemLanguage {
    CPP        ("CPP",        "cpp"),
    SQL        ("SQL",        "sql"),
    JAVASCRIPT ("JAVASCRIPT", "javascript");

    /** JSON / database value. */
    private final String value;

    /** Language identifier expected by the judge service. */
    private final String judgeLanguageId;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ProblemLanguage fromValue(String value) {
        return ProblemLanguage.valueOf(ProblemLanguage.class, value.toUpperCase());
    }
}
