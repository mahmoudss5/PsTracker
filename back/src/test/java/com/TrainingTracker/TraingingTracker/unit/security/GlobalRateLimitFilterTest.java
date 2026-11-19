package com.TrainingTracker.TraingingTracker.unit.security;

import com.TrainingTracker.TraingingTracker.Security.RateLimting.ClientIpResolver;
import com.TrainingTracker.TraingingTracker.Security.RateLimting.GlobalRateLimitFilter;
import com.TrainingTracker.TraingingTracker.Security.RateLimting.RateLimitDecision;
import com.TrainingTracker.TraingingTracker.Security.RateLimting.RateLimiterUnavailableException;
import com.TrainingTracker.TraingingTracker.Security.RateLimting.RedisRateLimiter;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalRateLimitFilterTest {

    @Mock
    private RedisRateLimiter rateLimiter;

    @Mock
    private ClientIpResolver clientIpResolver;

    @Mock
    private CorsConfigurationSource corsConfigurationSource;

    @Mock
    private FilterChain chain;

    private GlobalRateLimitFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        filter = new GlobalRateLimitFilter(rateLimiter, clientIpResolver, corsConfigurationSource, 100, 60);
        request = new MockHttpServletRequest("GET", "/api/teams");
        request.setServletPath("/api/teams");
        response = new MockHttpServletResponse();
    }

    @Test
    void allowsRequestUnderSharedIpQuota() throws Exception {
        when(clientIpResolver.resolveClientIp(request)).thenReturn("192.0.2.1");
        when(rateLimiter.checkRateLimit("rl:global:ip:192.0.2.1", 100, 60_000L))
                .thenReturn(new RateLimitDecision(true, 99, 0));

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertEquals(200, response.getStatus());
    }

    @Test
    void blocksExceededQuotaAndReturnsRetryAfter() throws Exception {
        when(clientIpResolver.resolveClientIp(request)).thenReturn("192.0.2.1");
        when(rateLimiter.checkRateLimit("rl:global:ip:192.0.2.1", 100, 60_000L))
                .thenReturn(new RateLimitDecision(false, 0, 1_500));

        filter.doFilter(request, response, chain);

        assertEquals(429, response.getStatus());
        assertEquals("2", response.getHeader("Retry-After"));
        assertEquals("no-store", response.getHeader("Cache-Control"));
        assertTrue(response.getContentAsString().contains("\"retryAfterSeconds\":2"));
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void blockedCrossOriginRequestIncludesAllowedCorsHeaders() throws Exception {
        request.addHeader(HttpHeaders.ORIGIN, "https://app.test");
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://app.test"));
        configuration.setAllowCredentials(true);
        when(corsConfigurationSource.getCorsConfiguration(request)).thenReturn(configuration);
        when(clientIpResolver.resolveClientIp(request)).thenReturn("192.0.2.1");
        when(rateLimiter.checkRateLimit("rl:global:ip:192.0.2.1", 100, 60_000L))
                .thenReturn(new RateLimitDecision(false, 0, 10));

        filter.doFilter(request, response, chain);

        assertEquals("https://app.test", response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
        assertEquals("true", response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS));
        assertEquals("1", response.getHeader("Retry-After"));
    }

    @Test
    void redisFailureReturnsServiceUnavailableWithoutCallingApi() throws Exception {
        when(clientIpResolver.resolveClientIp(request)).thenReturn("192.0.2.1");
        when(rateLimiter.checkRateLimit("rl:global:ip:192.0.2.1", 100, 60_000L))
                .thenThrow(new RateLimiterUnavailableException(new RuntimeException("redis down")));

        filter.doFilter(request, response, chain);

        assertEquals(503, response.getStatus());
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void nonApiRequestBypassesRateLimit() throws Exception {
        request.setServletPath("/actuator/health");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(clientIpResolver, rateLimiter);
    }

    @Test
    void corsPreflightBypassesRateLimit() throws Exception {
        request.setMethod("OPTIONS");
        request.addHeader(HttpHeaders.ORIGIN, "https://app.test");
        request.addHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(clientIpResolver, rateLimiter);
    }
}
