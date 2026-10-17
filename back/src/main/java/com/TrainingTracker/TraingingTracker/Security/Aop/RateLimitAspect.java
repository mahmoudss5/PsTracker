
package com.TrainingTracker.TraingingTracker.Security.Aop;

import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.RateLimitExceededException;
import com.TrainingTracker.TraingingTracker.Security.RateLimitIdentity;
import com.TrainingTracker.TraingingTracker.Security.RateLimting.ClientIpResolver;
import com.TrainingTracker.TraingingTracker.Security.RateLimting.RateLimitDecision;
import com.TrainingTracker.TraingingTracker.Security.RateLimting.RedisRateLimiter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RedisRateLimiter rateLimiter;
    private final ClientIpResolver clientIpResolver;

    @Around("@annotation(rateLimit)")
    public Object checkRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {

        // 1. Validate annotation configuration
        if (rateLimit.requests() <= 0 || rateLimit.windowSeconds() <= 0) {
            throw new IllegalArgumentException(
                    "Rate limit configuration must be positive"
            );
        }

        // 2. Resolve client identity
        String identity = switch (rateLimit.identity()) {
            case IP -> resolveIpIdentity();
            case USER -> resolveUserIdentity();
        };

        // 3. Identify the operation
        String operation = rateLimit.key().isBlank()
                ? joinPoint.getSignature().toLongString()
                : rateLimit.key();

        // 4. Build Redis key
        String redisKey = "rl:endpoint:"
                + operation
                + ":"
                + identity;

        // 5. Execute Redis Lua script. RedisRateLimiter normalizes Redis failures
        // to RateLimiterUnavailableException, which is handled centrally.
        RateLimitDecision decision = rateLimiter.checkRateLimit(
                redisKey,
                rateLimit.requests(),
                rateLimit.windowSeconds() * 1000L
        );

        // 6. Reject if limit exceeded
        if (!decision.isAllowed()) {
            throw new RateLimitExceededException(
                    decision.retryAfterMs()
            );
        }

        // 7. Execute original method
        return joinPoint.proceed();
    }


    private String resolveIpIdentity() {

        ServletRequestAttributes attributes =
                (ServletRequestAttributes)
                        RequestContextHolder.currentRequestAttributes();

        HttpServletRequest request = attributes.getRequest();

        String clientIp = clientIpResolver.resolveClientIp(request);

        return "ip:" + clientIp;
    }

    private String resolveUserIdentity() {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null
                || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }

        return "user:" + auth.getName();
    }
}
