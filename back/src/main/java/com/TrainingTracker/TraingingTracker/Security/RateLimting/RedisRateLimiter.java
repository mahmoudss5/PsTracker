package com.TrainingTracker.TraingingTracker.Security.RateLimting;


import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RedisRateLimiter {

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<List> slidingWindowScript;

    public RateLimitDecision checkRateLimit(String key, int limit, long windowMs) {
        if (key == null || key.isBlank()
                || limit <= 0 || windowMs <= 0) {
            throw new IllegalArgumentException(
                    "Invalid rate limit configuration"
            );
        }
        String requestId = UUID.randomUUID().toString();
        try {
            List<?> result = redisTemplate.execute(
                    slidingWindowScript,
                    List.of(key),
                    String.valueOf(limit),
                    String.valueOf(windowMs),
                    requestId
            );

            if (result == null || result.size() < 3) {
                throw new IllegalStateException("Invalid response from Redis script");
            }

            long allowedFlag = asLong(result.get(0));
            long remainingRequests = asLong(result.get(1));
            long retryAfterMs = asLong(result.get(2));
            return new RateLimitDecision(allowedFlag == 1, remainingRequests, retryAfterMs);
        } catch (DataAccessException | IllegalStateException ex) {
            throw new RateLimiterUnavailableException(ex);
        }
    }

    private long asLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String stringValue) {
            try {
                return Long.parseLong(stringValue);
            } catch (NumberFormatException ex) {
                throw new IllegalStateException("Invalid numeric value from Redis script: " + value, ex);
            }
        }
        throw new IllegalStateException("Invalid value from Redis script: " + value);
    }

}
