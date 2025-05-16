package com.vi5hnu.codesprout.services;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class BucketService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String userId, int capacity, int time, TimeUnit unit) {
        Duration duration = Duration.ofMillis(unit.toMillis(time));
        Bandwidth limit = Bandwidth.builder()
                .capacity(capacity)
                .refillIntervally(capacity, duration)
                .build();
        return cache.computeIfAbsent(userId, id -> Bucket.builder()
                .addLimit(limit)
                .build());
    }
}
