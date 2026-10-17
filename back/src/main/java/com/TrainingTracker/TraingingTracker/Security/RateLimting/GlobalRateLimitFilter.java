package com.TrainingTracker.TraingingTracker.Security.RateLimting;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterProperties;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(SecurityFilterProperties.DEFAULT_FILTER_ORDER - 1)
@AllArgsConstructor
public class GlobalRateLimitFilter extends OncePerRequestFilter {
    private final RedisRateLimiter rateLimiter;
    private final ClientIpResolver clientIpResolver;
    private final CorsConfigurationSource corsConfigurationSource;
    private static final int MAX_REQUESTS = 100;
    private static final long WINDOW_MS = 60_000L;


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return CorsUtils.isPreFlightRequest(request)
                || !request.getServletPath().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Identify the client
        String clientIp = clientIpResolver.resolveClientIp(request);

        // Shared quota across all API endpoints for this IP
        String redisKey = "rl:global:ip:" + clientIp;

        RateLimitDecision decision;

        try {
            decision = rateLimiter.checkRateLimit(
                    redisKey,
                    MAX_REQUESTS,
                    WINDOW_MS
            );
        } catch (RateLimiterUnavailableException ex) {
            // Fail closed if Redis is unavailable
            addCorsHeaders(request, response);
            response.sendError(
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Rate limiter unavailable"
            );
            return;
        }

        if (!decision.isAllowed()) {

            long retryAfterSeconds = Math.max(
                    1,
                    (long) Math.ceil(decision.retryAfterMs() / 1000.0)
            );

            addCorsHeaders(request, response);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setHeader(
                    "Retry-After",
                    String.valueOf(retryAfterSeconds)
            );

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-store");

            response.getWriter().write(
                    "{\"error\":\"too_many_requests\","
                            + "\"retryAfterSeconds\":"
                            + retryAfterSeconds + "}"
            );

            return;
        }

        filterChain.doFilter(request, response);
    }

    private void addCorsHeaders(HttpServletRequest request, HttpServletResponse response) {
        if (!CorsUtils.isCorsRequest(request)) {
            return;
        }

        CorsConfiguration configuration = corsConfigurationSource.getCorsConfiguration(request);
        if (configuration == null) {
            return;
        }

        String allowedOrigin = configuration.checkOrigin(request.getHeader(HttpHeaders.ORIGIN));
        if (allowedOrigin == null) {
            return;
        }

        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, allowedOrigin);
        response.addHeader(HttpHeaders.VARY, HttpHeaders.ORIGIN);
        if (Boolean.TRUE.equals(configuration.getAllowCredentials())) {
            response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }
    }

}
