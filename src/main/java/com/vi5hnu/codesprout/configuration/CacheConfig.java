package com.vi5hnu.codesprout.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    public static final String PROBLEMS_CACHE       = "problems";
    public static final String PROBLEM_TAGS_CACHE   = "problem_tags";
    public static final String FOLDERS_CACHE        = "folders";
    public static final String PRESIGNED_URLS_CACHE = "presigned_urls";

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(
                buildCache(PROBLEMS_CACHE,       10,  500),
                buildCache(PROBLEM_TAGS_CACHE,   10,  500),
                buildCache(FOLDERS_CACHE,        10,  500),
                // 50-min TTL: URL expiry is 1 hour, so cached entries are replaced
                // 10 min before the presigned URL expires — the threshold window.
                buildCache(PRESIGNED_URLS_CACHE, 50, 1000)
        ));
        return manager;
    }

    private static CaffeineCache buildCache(String name, int ttlMinutes, int maxSize) {
        return new CaffeineCache(name, Caffeine.newBuilder()
                .expireAfterWrite(ttlMinutes, TimeUnit.MINUTES)
                .maximumSize(maxSize)
                .build());
    }
}
