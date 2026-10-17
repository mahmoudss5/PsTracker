package com.TrainingTracker.TraingingTracker.Security.RateLimting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisRateLimiterTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private RedisScript<List> script;

    @Test
    void mapsAllowedScriptResultAndPassesRequestId() {
        when(redisTemplate.execute(eq(script), eq(List.of("rl:key")), any(Object[].class)))
                .thenReturn(List.of(1L, 4L, 0L));

        RedisRateLimiter limiter = new RedisRateLimiter(redisTemplate, script);
        RateLimitDecision decision = limiter.checkRateLimit("rl:key", 5, 60_000L);

        assertTrue(decision.isAllowed());
        assertEquals(4L, decision.remainingRequests());
        assertEquals(0L, decision.retryAfterMs());

        ArgumentCaptor<Object[]> argsCaptor = ArgumentCaptor.forClass(Object[].class);
        verify(redisTemplate).execute(eq(script), eq(List.of("rl:key")), argsCaptor.capture());
        Object[] args = argsCaptor.getValue();
        assertEquals("5", args[0]);
        assertEquals("60000", args[1]);
        assertTrue(args[2] instanceof String && !((String) args[2]).isBlank());
    }

    @Test
    void mapsStringScriptResult() {
        when(redisTemplate.execute(eq(script), eq(List.of("rl:key")), any(Object[].class)))
                .thenReturn(List.of("1", "4", "0"));

        RedisRateLimiter limiter = new RedisRateLimiter(redisTemplate, script);
        RateLimitDecision decision = limiter.checkRateLimit("rl:key", 5, 60_000L);

        assertTrue(decision.isAllowed());
        assertEquals(4L, decision.remainingRequests());
        assertEquals(0L, decision.retryAfterMs());
    }

    @Test
    void mapsRejectedScriptResultAndRetryAfter() {
        when(redisTemplate.execute(eq(script), eq(List.of("rl:key")), any(Object[].class)))
                .thenReturn(List.of(0L, 0L, 2_500L));

        RedisRateLimiter limiter = new RedisRateLimiter(redisTemplate, script);
        RateLimitDecision decision = limiter.checkRateLimit("rl:key", 1, 60_000L);

        assertFalse(decision.isAllowed());
        assertEquals(0L, decision.remainingRequests());
        assertEquals(2_500L, decision.retryAfterMs());
    }

    @Test
    void rejectsInvalidConfiguration() {
        RedisRateLimiter limiter = new RedisRateLimiter(redisTemplate, script);

        assertThrows(IllegalArgumentException.class,
                () -> limiter.checkRateLimit("", 5, 60_000L));
    }

    @Test
    void rateLimitScriptIsPackagedAtConfiguredPath() {
        assertTrue(new ClassPathResource("scripts/slidingWindow.lua").exists());
    }
}
