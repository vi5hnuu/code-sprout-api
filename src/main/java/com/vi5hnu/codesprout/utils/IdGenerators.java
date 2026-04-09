package com.vi5hnu.codesprout.utils;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Central ID factory for all entities and tokens.
 *
 * <pre>
 * generateIdWithPrefix("PID")     → "PID01HV3RKMQ7ABC..."  (32 chars, UUID-based, existing behaviour)
 * ulid()                          → "01HV3RKMQ7..."        (26 chars, time-sortable)
 * prefixedUlid("PID")             → "PID01HV3RKMQ7..."     (32 chars, time-sortable with prefix)
 * shortRandom(8)                  → "A3FX9K2M"             (N chars, invite/share codes)
 * numeric(6)                      → "482913"               (N digits, OTPs / PINs)
 * uuid()                          → "550e8400..."           (32 hex chars, random opaque ID)
 * </pre>
 */
public class IdGenerators {

    private static final int MIN_LENGTH = 32;
    private static final int MAX_LENGTH = 64;

    /** Crockford Base32 — omits I/L/O/U to reduce misreading. */
    private static final char[] BASE32      = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();
    private static final char[] ALPHANUMERIC = "ABCDEFGHJKMNPQRSTVWXYZabcdefghjkmnpqrstvwxyz0123456789".toCharArray();
    private static final SecureRandom RNG   = new SecureRandom();

    private IdGenerators() {}

    // ── Existing (UUID-based) ────────────────────────────────────────────────

    /**
     * Prefix + UUID truncated / padded to {@code length} chars.
     * Preserves original behaviour used throughout the codebase.
     */
    public static String generateIdWithPrefix(String prefix, int length) {
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            throw new IllegalArgumentException(
                String.format("Length must be between %d and %d", MIN_LENGTH, MAX_LENGTH));
        }
        return build(prefix, length);
    }

    /** Prefix + UUID at default 32-char length. */
    public static String generateIdWithPrefix(String prefix) {
        return build(prefix, MIN_LENGTH);
    }

    private static String build(String prefix, int length) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        StringBuilder id = new StringBuilder(prefix).append(uuid);
        if (id.length() > length) return id.substring(0, length);
        while (id.length() < length) id.append('0');
        return id.toString();
    }

    // ── Time-sortable (ULID) ─────────────────────────────────────────────────

    /**
     * ULID — 26-char Crockford Base32, lexicographically sortable by creation time.
     * Ideal as a primary key when you want efficient range scans or visible ordering.
     * Format: [10 timestamp chars][16 random chars]
     */
    public static String ulid() {
        long ts = System.currentTimeMillis();
        char[] out = new char[26];

        // 10 chars = 48-bit millisecond timestamp, most-significant first
        for (int i = 9; i >= 0; i--) { out[i] = BASE32[(int)(ts & 0x1F)]; ts >>= 5; }

        // 16 chars = 80 random bits
        long r1 = RNG.nextLong(), r2 = RNG.nextLong();
        for (int i = 10; i < 18; i++) { out[i] = BASE32[(int)(r1 & 0x1F)]; r1 >>= 5; }
        for (int i = 18; i < 26; i++) { out[i] = BASE32[(int)(r2 & 0x1F)]; r2 >>= 5; }

        return new String(out);
    }

    /**
     * Readable prefix + ULID truncated to 32 chars.
     * Keeps time-sortability while making entity type obvious in logs/DBs.
     * e.g. {@code prefixedUlid("PID")} → {@code "PID01HV3RKMQ7..."}
     */
    public static String prefixedUlid(String prefix) {
        return (prefix + ulid()).substring(0, 32);
    }

    // ── Random ───────────────────────────────────────────────────────────────

    /** 32-char UUID without hyphens. Use when sortability is not needed. */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Random alphanumeric string of {@code length} chars (Crockford-safe alphabet).
     * Use for invite codes, share tokens, short display IDs.
     */
    public static String shortRandom(int length) {
        char[] out = new char[length];
        for (int i = 0; i < length; i++) out[i] = ALPHANUMERIC[RNG.nextInt(ALPHANUMERIC.length)];
        return new String(out);
    }

    /**
     * Numeric-only string of {@code length} digits.
     * Use for OTPs, PIN codes, verification codes.
     */
    public static String numeric(int length) {
        char[] out = new char[length];
        for (int i = 0; i < length; i++) out[i] = (char)('0' + RNG.nextInt(10));
        return new String(out);
    }
}
