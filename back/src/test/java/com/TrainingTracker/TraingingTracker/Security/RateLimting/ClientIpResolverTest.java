package com.TrainingTracker.TraingingTracker.Security.RateLimting;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientIpResolverTest {

    @Test
    void usesForwardedClientIpOnlyWhenRemoteProxyIsTrusted() {
        ClientIpResolver resolver = new ClientIpResolver("10.0.0.0/8");
        MockHttpServletRequest request = request("10.0.0.10");
        request.addHeader("X-Forwarded-For", "198.51.100.7, 10.0.0.11");

        assertEquals("198.51.100.7", resolver.resolveClientIp(request));
    }

    @Test
    void ignoresForwardedHeaderFromUntrustedRemoteAddress() {
        ClientIpResolver resolver = new ClientIpResolver("10.0.0.0/8");
        MockHttpServletRequest request = request("198.51.100.20");
        request.addHeader("X-Forwarded-For", "203.0.113.5");

        assertEquals("198.51.100.20", resolver.resolveClientIp(request));
    }

    private MockHttpServletRequest request(String remoteAddress) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(remoteAddress);
        return request;
    }
}
