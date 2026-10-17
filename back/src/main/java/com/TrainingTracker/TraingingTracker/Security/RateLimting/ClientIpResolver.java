package com.TrainingTracker.TraingingTracker.Security.RateLimting;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ClientIpResolver {

    private final List<IpAddressMatcher> trustedProxyMatchers;

    public ClientIpResolver(
            @Value("${app.trusted-proxy-addresses:}") String trustedProxyAddresses) {
        this.trustedProxyMatchers = Arrays.stream(trustedProxyAddresses.split(","))
                .map(String::trim)
                .filter(address -> !address.isBlank())
                .map(IpAddressMatcher::new)
                .toList();
    }

    public String resolveClientIp(HttpServletRequest request) {
        String remoteAddress = normalize(request.getRemoteAddr());
        if (remoteAddress == null || !isTrustedProxy(remoteAddress)) {
            return remoteAddress == null ? "unknown" : remoteAddress;
        }

        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor == null || forwardedFor.isBlank()) {
            return remoteAddress;
        }

        String currentAddress = remoteAddress;
        String[] forwardedAddresses = forwardedFor.split(",");
        for (int index = forwardedAddresses.length - 1; index >= 0; index--) {
            if (!isTrustedProxy(currentAddress)) {
                break;
            }

            String candidate = normalize(forwardedAddresses[index]);
            if (candidate == null) {
                break;
            }
            currentAddress = candidate;
        }

        return currentAddress;
    }

    private boolean isTrustedProxy(String address) {
        return trustedProxyMatchers.stream().anyMatch(matcher -> matcher.matches(address));
    }

    private String normalize(String address) {
        if (address == null) {
            return null;
        }

        String normalized = address.trim();
        if (normalized.isBlank()) {
            return null;
        }

        if (normalized.startsWith("[") && normalized.endsWith("]")) {
            normalized = normalized.substring(1, normalized.length() - 1);
        }

        return normalized;
    }
}
