package com.portfolio.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {

    private final Map<String, Bucket> contactBuckets = new ConcurrentHashMap<>();

    public Bucket resolveContactBucket(String ipAddress) {
        return contactBuckets.computeIfAbsent(ipAddress, this::newContactBucket);
    }

    private Bucket newContactBucket(String ip) {
        Bandwidth limit = Bandwidth.classic(
                3,                                  // 3 requests
                Refill.intervally(3, Duration.ofHours(1)) // refill every 1 hour
        );
        return Bucket.builder().addLimit(limit).build();
    }
}