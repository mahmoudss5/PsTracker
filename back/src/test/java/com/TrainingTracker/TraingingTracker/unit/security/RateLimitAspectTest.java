package com.TrainingTracker.TraingingTracker.unit.security;

import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.RateLimitExceededException;
import com.TrainingTracker.TraingingTracker.Security.Aop.RateLimit.RateLimit;
import com.TrainingTracker.TraingingTracker.Security.Aop.RateLimit.RateLimitAspect;
import com.TrainingTracker.TraingingTracker.Security.RateLimitIdentity;
import com.TrainingTracker.TraingingTracker.Security.RateLimting.ClientIpResolver;
import com.TrainingTracker.TraingingTracker.Security.RateLimting.RateLimitDecision;
import com.TrainingTracker.TraingingTracker.Security.RateLimting.RedisRateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimitAspectTest {

    @Mock
    private RedisRateLimiter rateLimiter;

    @Mock
    private ClientIpResolver clientIpResolver;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private RateLimit annotation;

    @AfterEach
    void clearContexts() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void authenticatedUserWithinLimitCanProceed() throws Throwable {
        authenticate("user@example.com");
        when(annotation.requests()).thenReturn(2);
        when(annotation.windowSeconds()).thenReturn(60);
        when(annotation.identity()).thenReturn(RateLimitIdentity.USER);
        when(annotation.key()).thenReturn("join-team");
        when(rateLimiter.checkRateLimit("rl:endpoint:join-team:user:user@example.com", 2, 60_000L))
                .thenReturn(new RateLimitDecision(true, 1, 0));
        Object expected = new Object();
        when(joinPoint.proceed()).thenReturn(expected);

        Object result = new RateLimitAspect(rateLimiter, clientIpResolver).checkRateLimit(joinPoint, annotation);

        assertSame(expected, result);
        verify(joinPoint).proceed();
    }

    @Test
    void exceededLimitRejectsBeforeRunningMethod() throws Throwable {
        authenticate("user@example.com");
        when(annotation.requests()).thenReturn(2);
        when(annotation.windowSeconds()).thenReturn(60);
        when(annotation.identity()).thenReturn(RateLimitIdentity.USER);
        when(annotation.key()).thenReturn("join-team");
        when(rateLimiter.checkRateLimit("rl:endpoint:join-team:user:user@example.com", 2, 60_000L))
                .thenReturn(new RateLimitDecision(false, 0, 1_500));

        RateLimitExceededException error = assertThrows(RateLimitExceededException.class,
                () -> new RateLimitAspect(rateLimiter, clientIpResolver).checkRateLimit(joinPoint, annotation));

        assertEquals(1_500L, error.getRetryAfterMs());
        verify(joinPoint, never()).proceed();
    }

    @Test
    void invalidLimitConfigurationFailsBeforeRedisCall() {
        when(annotation.requests()).thenReturn(0);

        assertThrows(IllegalArgumentException.class,
                () -> new RateLimitAspect(rateLimiter, clientIpResolver).checkRateLimit(joinPoint, annotation));

        verifyNoInteractions(rateLimiter);
    }

    @Test
    void userIdentityRequiresAuthentication() {
        when(annotation.requests()).thenReturn(2);
        when(annotation.windowSeconds()).thenReturn(60);
        when(annotation.identity()).thenReturn(RateLimitIdentity.USER);

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> new RateLimitAspect(rateLimiter, clientIpResolver).checkRateLimit(joinPoint, annotation));

        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatusCode());
        verifyNoInteractions(rateLimiter);
    }

    @Test
    void ipIdentityUsesResolvedAddressAndMethodNameWhenKeyIsBlank() throws Throwable {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(annotation.requests()).thenReturn(3);
        when(annotation.windowSeconds()).thenReturn(10);
        when(annotation.identity()).thenReturn(RateLimitIdentity.IP);
        when(annotation.key()).thenReturn("");
        when(clientIpResolver.resolveClientIp(request)).thenReturn("192.0.2.1");
        Signature signature = org.mockito.Mockito.mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.toLongString()).thenReturn("TeamController.joinTeam(..)");
        when(rateLimiter.checkRateLimit("rl:endpoint:TeamController.joinTeam(..):ip:192.0.2.1", 3, 10_000L))
                .thenReturn(new RateLimitDecision(true, 2, 0));

        new RateLimitAspect(rateLimiter, clientIpResolver).checkRateLimit(joinPoint, annotation);

        verify(joinPoint).proceed();
    }

    private void authenticate(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null, List.of()));
    }
}
