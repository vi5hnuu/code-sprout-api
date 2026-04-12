package com.vi5hnu.codesprout.utils;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Central ID factory for all entities and tokens.
 *
 * <pre>
 * Strategy             Example output                  Use case
 * ─────────────────────────────────────────────────────────────────────
 * prefixUlid("USR")  → "USR_01HV3RKMQ7ABCDE..."      Entity PKs (time-sortable)
 * prefixDate("INV")  → "INV_20260411_A3FX9K"          Human-readable dated IDs
 * shortRandom(8)     → "A3FX9K2M"                     Invite / share codes
 * numeric(6)         → "482913"                        OTPs / PINs
 * uuid()             → "550e8400e29b41d4..."           Opaque random IDs
 * </pre>
 */
public class IdGenerators {

    /** Crockford Base32 — omits I/L/O/U to reduce visual misreading. */
    private static final char[] BASE32       = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();
    private static final char[] ALPHANUMERIC = "ABCDEFGHJKMNPQRSTVWXYZabcdefghjkmnpqrstvwxyz0123456789".toCharArray();
    private static final SecureRandom        RNG          = new SecureRandom();
    private static final DateTimeFormatter   DATE_COMPACT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private IdGenerators() {}

    // ── Time-sortable ────────────────────────────────────────────────────────

    /**
     * {@code PREFIX_<ULID>} — lexicographically sortable by creation time.
     * <p>
     * The full 26-char ULID is preserved so the random suffix stays intact.
     * Example: {@code "USR_01HV3RKMQ7ABCDEFGHJKMN"}
     */
    public static String prefixUlid(String prefix) {
        return prefix + "_" + ulid();
    }

    /**
     * Raw ULID — 26 Crockford Base32 chars, sortable by creation time.
     * Format: [10 timestamp chars][16 random chars]
     */
    public static String ulid() {
        long ts = System.currentTimeMillis();
        char[] out = new char[26];

        // Timestamp: 10 chars, MSB first (index 0 = most significant → sortable)
        for (int i = 9; i >= 0; i--) {
            out[i] = BASE32[(int) (ts & 0x1F)];
            ts >>= 5;
        }

        // Random: 16 chars = 80 bits (two longs, 40 bits each)
        long r1 = RNG.nextLong();
        long r2 = RNG.nextLong();
        for (int i = 10; i < 18; i++) { out[i] = BASE32[(int) (r1 & 0x1F)]; r1 >>= 5; }
        for (int i = 18; i < 26; i++) { out[i] = BASE32[(int) (r2 & 0x1F)]; r2 >>= 5; }

        return new String(out);
    }

    // ── Date-scoped ──────────────────────────────────────────────────────────

    /**
     * {@code PREFIX_yyyyMMdd_<random>} — human-readable, date-grouped.
     * Suitable for invoices, tickets, order numbers, run IDs.
     * Example: {@code "INV_20260411_A3FX9K"}
     *
     * @param prefix       entity prefix (e.g. "INV", "ORD")
     * @param randomLength length of the random suffix (recommend 6–12)
     */
    public static String prefixDate(String prefix, int randomLength) {
        return prefix + "_" + LocalDate.now().format(DATE_COMPACT) + "_" + shortRandom(randomLength);
    }

    /** {@code PREFIX_yyyyMMdd_<8-char random>} with default random length of 8. */
    public static String prefixDate(String prefix) {
        return prefixDate(prefix, 8);
    }

    /**
     * {@code PREFIX_yyyyMMdd_<sequential-safe random>} using numeric suffix.
     * Useful when the consumer needs a purely numeric suffix (e.g. batch jobs).
     * Example: {@code "RUN_20260411_003847"}
     */
    public static String prefixDateNumeric(String prefix, int digitCount) {
        return prefix + "_" + LocalDate.now().format(DATE_COMPACT) + "_" + numeric(digitCount);
    }

    // ── Random ───────────────────────────────────────────────────────────────

    /**
     * Random alphanumeric string (Crockford-safe alphabet — no I/L/O/U).
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
        for (int i = 0; i < length; i++) out[i] = (char) ('0' + RNG.nextInt(10));
        return new String(out);
    }

    /** 32-char UUID without hyphens. Use when sortability is not needed. */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /** {@code PREFIX_<UUID>} — opaque, non-sortable. */
    public static String prefixUuid(String prefix) {
        return prefix + "_" + uuid();
    }

    // ── Deprecated (kept for DB-format compatibility) ─────────────────────────

    /** @deprecated Use {@link #prefixUlid(String)} for new entities. */
    @Deprecated(forRemoval = true)
    public static String prefixedUlid(String prefix) {
        String id = prefix + ulid();
        return id.length() > 32 ? id.substring(0, 32) : id;
    }

    /** @deprecated Use {@link #prefixUlid(String)} for new entities. */
    @Deprecated(forRemoval = true)
    public static String generateIdWithPrefix(String prefix) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String id = prefix + uuid;
        return id.length() > 32 ? id.substring(0, 32) : id;
    }
}
