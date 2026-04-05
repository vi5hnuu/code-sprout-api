package com.vi5hnu.codesprout.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Distributed sliding-window rate limiter backed by Redis.
 *
 * Uses a per-user sorted set where each token is a unique member with the
 * request timestamp (ms) as its score.  A Lua script atomically:
 *   1. Evicts entries outside the current window
 *   2. Counts remaining entries
 *   3. Adds a new entry only if below the limit
 *
 * All three operations run in a single Redis round-trip (no WATCH/MULTI),
 * so there is no ABA race and the overhead is one network call per request.
 *
 * Fail-open on Redis errors: if Redis is unreachable the call is allowed
 * through so a Redis outage never prevents code execution.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class RedisRateLimitService {

    // Lua script:
    //   KEYS[1] = "rl:{userId}"
    //   ARGV[1] = current time in ms
    //   ARGV[2] = window size in ms
    //   ARGV[3] = max requests in window
    //   ARGV[4] = unique member suffix (UUID) — prevents score collisions
    private static final String LUA = """
            local key  = KEYS[1]
            local now  = tonumber(ARGV[1])
            local win  = tonumber(ARGV[2])
            local lim  = tonumber(ARGV[3])
            local uid  = ARGV[4]
            redis.call('ZREMRANGEBYSCORE', key, '-inf', now - win)
            local cnt = tonumber(redis.call('ZCARD', key))
            if cnt < lim then
                redis.call('ZADD', key, now, now .. ':' .. uid)
                redis.call('PEXPIRE', key, win * 2)
                return 1
            end
            return 0
            """;

    private static final RedisScript<Long> SCRIPT = RedisScript.of(LUA, Long.class);

    private final StringRedisTemplate redis;

    /**
     * Attempt to consume one token for the given identity within the window.
     *
     * @param identity  unique key — authenticated user ID or client IP
     * @param limit     max requests allowed per window
     * @param windowMs  sliding window duration in milliseconds
     * @return {@code true} if the request is within the limit, {@code false} if rate-limited
     */
    public boolean tryConsume(String identity, int limit, long windowMs) {
        try {
            Long result = redis.execute(
                    SCRIPT,
                    List.of("rl:" + identity),
                    String.valueOf(System.currentTimeMillis()),
                    String.valueOf(windowMs),
                    String.valueOf(limit),
                    UUID.randomUUID().toString()
            );
            return Long.valueOf(1L).equals(result);
        } catch (Exception e) {
            // Fail-open: Redis down should not block users from submitting code.
            log.warn("Redis rate-limit check failed (fail-open): {}", e.getMessage());
            return true;
        }
    }
}
