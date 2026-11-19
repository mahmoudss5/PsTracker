package com.TrainingTracker.TraingingTracker.unit.security;

import com.TrainingTracker.TraingingTracker.Security.User.CustomUserDetailsService;
import com.TrainingTracker.TraingingTracker.Security.WebSocket.WebSocketAuthInterceptor;
import com.TrainingTracker.TraingingTracker.Security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSocketAuthInterceptorTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private MessageChannel channel;

    @Test
    void validConnectTokenSetsAuthenticatedUser() {
        UserDetails user = org.springframework.security.core.userdetails.User.withUsername("user@example.com")
                .password("unused").authorities("Trainee").build();
        when(jwtService.extractUsername("valid")).thenReturn("user@example.com");
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(user);
        when(jwtService.isTokenValid("valid", user)).thenReturn(true);

        Message<?> result = interceptor().preSend(message(StompCommand.CONNECT, "Bearer valid"), channel);

        Authentication authentication = (Authentication) StompHeaderAccessor.wrap(result).getUser();
        assertSame(user, authentication.getPrincipal());
        assertEquals("user@example.com", authentication.getName());
    }

    @Test
    void connectWithoutBearerHeaderIsRejected() {
        AccessDeniedException error = assertThrows(AccessDeniedException.class,
                () -> interceptor().preSend(message(StompCommand.CONNECT, null), channel));

        assertEquals("Missing or invalid Authorization header", error.getMessage());
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void invalidJwtIsRejected() {
        when(jwtService.extractUsername("bad")).thenThrow(new IllegalArgumentException("invalid"));

        AccessDeniedException error = assertThrows(AccessDeniedException.class,
                () -> interceptor().preSend(message(StompCommand.CONNECT, "Bearer bad"), channel));

        assertEquals("Invalid token", error.getMessage());
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void tokenThatFailsValidationIsRejected() {
        UserDetails user = org.springframework.security.core.userdetails.User.withUsername("user@example.com")
                .password("unused").authorities("Trainee").build();
        when(jwtService.extractUsername("expired")).thenReturn("user@example.com");
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(user);
        when(jwtService.isTokenValid("expired", user)).thenReturn(false);

        AccessDeniedException error = assertThrows(AccessDeniedException.class,
                () -> interceptor().preSend(message(StompCommand.CONNECT, "Bearer expired"), channel));

        assertEquals("Invalid token", error.getMessage());
    }

    @Test
    void nonConnectMessagePassesThroughWithoutAuthentication() {
        Message<?> message = message(StompCommand.SEND, null);

        assertSame(message, interceptor().preSend(message, channel));

        verifyNoInteractions(jwtService, userDetailsService);
    }

    private WebSocketAuthInterceptor interceptor() {
        return new WebSocketAuthInterceptor(jwtService, userDetailsService);
    }

    private Message<byte[]> message(StompCommand command, String authorization) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(command);
        if (authorization != null) {
            accessor.setNativeHeader("Authorization", authorization);
        }
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }
}
