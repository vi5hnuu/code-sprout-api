package com.vi5hnu.codesprout.utils;

import java.util.UUID;

public class IdGenerators {

    private static final int MIN_LENGTH = 32; // Minimum length requirement
    private static final int MAX_LENGTH = 64; // Minimum length requirement
    /**
     * Generates a unique ID with the specified prefix and length.
     *
     * @param prefix The prefix to be added to the generated ID.
     * @param length The desired total length of the generated ID (including the prefix).
     * @return The generated unique ID.
     * @throws IllegalArgumentException if the specified length is less than the minimum required length.
     */
    public static String generateIdWithPrefix(String prefix, int length) {
        if (length < MIN_LENGTH || length>MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("Length must be at least %s and at max %s characters",MIN_LENGTH,MAX_LENGTH));
        }

        String uuid = UUID.randomUUID().toString().replace("-", "");
        StringBuilder id = new StringBuilder(prefix + uuid);

        if (id.length() > length) {
            id = new StringBuilder(id.substring(0, length));
        } else {
            while (id.length() < length) id.append("0");
        }

        return id.toString();
    }
    public static String generateIdWithPrefix(String prefix) {
        int length=MIN_LENGTH;
        String uuid = UUID.randomUUID().toString().replace("-", "");
        StringBuilder id = new StringBuilder(prefix + uuid);

        if (id.length() > length) {
            id = new StringBuilder(id.substring(0, length));
        } else {
            while (id.length() < length) id.append("0");
        }
        return id.toString();
    }
}

