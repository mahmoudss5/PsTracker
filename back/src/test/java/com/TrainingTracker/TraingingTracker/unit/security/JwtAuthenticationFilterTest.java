package com.TrainingTracker.TraingingTracker.unit.security;

import com.TrainingTracker.TraingingTracker.Security.jwt.JwtAuthenticationFilter;
import com.TrainingTracker.TraingingTracker.Security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain chain;

    private JwtAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void passesRequestWithoutBearerHeaderAlongUnauthenticated() throws Exception {
        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void ignoresMalformedAuthorizationHeader() throws Exception {
        request.addHeader("Authorization", "Basic credentials");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void invalidJwtDoesNotAuthenticateRequest() throws Exception {
        request.addHeader("Authorization", "Bearer bad");
        when(jwtService.extractUsername("bad")).thenThrow(new IllegalArgumentException("invalid"));

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void validJwtAuthenticatesUserBeforeContinuingFilterChain() throws Exception {
        request.addHeader("Authorization", "Bearer valid");
        UserDetails user = org.springframework.security.core.userdetails.User.withUsername("user@example.com")
                .password("unused").authorities("Trainee").build();
        when(jwtService.extractUsername("valid")).thenReturn("user@example.com");
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(user);
        when(jwtService.isTokenValid("valid", user)).thenReturn(true);

        filter.doFilter(request, response, chain);

        assertSame(user, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(chain).doFilter(request, response);
    }

    @Test
    void rejectedJwtDoesNotPopulateSecurityContext() throws Exception {
        request.addHeader("Authorization", "Bearer expired");
        UserDetails user = org.springframework.security.core.userdetails.User.withUsername("user@example.com")
                .password("unused").authorities("Trainee").build();
        when(jwtService.extractUsername("expired")).thenReturn("user@example.com");
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(user);
        when(jwtService.isTokenValid("expired", user)).thenReturn(false);

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(request, response);
    }

    @Test
    void existingAuthenticationIsNotReplaced() throws Exception {
        request.addHeader("Authorization", "Bearer another");
        UsernamePasswordAuthenticationToken existing =
                new UsernamePasswordAuthenticationToken("existing@example.com", null, java.util.List.of());
        SecurityContextHolder.getContext().setAuthentication(existing);
        when(jwtService.extractUsername("another")).thenReturn("other@example.com");

        filter.doFilter(request, response, chain);

        assertSame(existing, SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(userDetailsService);
        verify(chain).doFilter(request, response);
    }
}
