package com.TrainingTracker.TraingingTracker.unit.Controllers.Controllers;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.AuthService;
import com.TrainingTracker.TraingingTracker.Controllers.AuthController;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth.AuthResponse;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth.SignInDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth.SignUpDto;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private void assertRefreshCookie(MockHttpServletResponse response, String value, long maxAge) {
        String cookie = response.getHeader(HttpHeaders.SET_COOKIE);
        assertNotNull(cookie);
        assertTrue(cookie.startsWith("refreshToken=" + value + ";"));
        assertTrue(cookie.contains("; Path=/api/auth"));
        assertTrue(cookie.contains("; Max-Age=" + maxAge));
        assertTrue(cookie.contains("; Secure"));
        assertTrue(cookie.contains("; HttpOnly"));
        assertTrue(cookie.contains("; SameSite=Strict"));
    }

    @Test
    void testLogin() {
        SignInDto dto = new SignInDto("john@example.com", "password123");
        AuthResponse auth = new AuthResponse("access-token", "refresh-token", 10L, "John", false);
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        when(authService.signIn(dto)).thenReturn(auth);

        ResponseEntity<AuthResponse> response = authController.login(dto, servletResponse);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(auth.token(), response.getBody().token());
        assertEquals(auth.userId(), response.getBody().userId());
        assertRefreshCookie(servletResponse, "refresh-token", 604800);
        verify(authService).signIn(dto);
    }

    @Test
    void testRegister() {
        SignUpDto dto = new SignUpDto("Coach John", "john@example.com", "john_cf", "password123", true);
        AuthResponse auth = new AuthResponse("access-token", "refresh-token", 10L, "Coach John", true);
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        when(authService.signUp(dto)).thenReturn(auth);

        ResponseEntity<AuthResponse> response = authController.register(dto, servletResponse);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(auth.token(), response.getBody().token());
        assertTrue(response.getBody().isCoach());
        assertRefreshCookie(servletResponse, "refresh-token", 604800);
        verify(authService).signUp(dto);
    }

    @Test
    void testLogoutClearsRefreshCookie() {
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();

        authController.logout("refresh-token", servletResponse);

        assertRefreshCookie(servletResponse, "", 0);
        verify(authService).logout();
    }

    @Test
    void testRefreshTokenRotatesCookie() {
        String refreshToken = "old-refresh-token";
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        when(authService.refreshToken(refreshToken)).thenReturn("new-access-token");
        when(authService.getNewRefreshToken(refreshToken)).thenReturn("new-refresh-token");

        ResponseEntity<Map<String, String>> response = authController.refreshToken(refreshToken, servletResponse);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Map.of("token", "new-access-token"), response.getBody());
        assertRefreshCookie(servletResponse, "new-refresh-token", 604800);
        verify(authService).refreshToken(refreshToken);
        verify(authService).getNewRefreshToken(refreshToken);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void testRefreshTokenWithoutCookieShouldFail(String refreshToken) {
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();

        ResponseEntity<Map<String, String>> response = authController.refreshToken(refreshToken, servletResponse);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        assertNull(servletResponse.getHeader(HttpHeaders.SET_COOKIE));
        verifyNoInteractions(authService);
    }

    @Test
    void testRefreshTokenWithInvalidTokenShouldFail() {
        String refreshToken = "invalid-token";
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        when(authService.refreshToken(refreshToken)).thenThrow(new IllegalArgumentException("Invalid refresh token"));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> authController.refreshToken(refreshToken, servletResponse));

        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatusCode());
        assertEquals("Invalid refresh token", error.getReason());
        assertNull(servletResponse.getHeader(HttpHeaders.SET_COOKIE));
        verify(authService).refreshToken(refreshToken);
        verify(authService, never()).getNewRefreshToken(refreshToken);
    }

    @Test
    void testRefreshTokenWhenRotationFails() {
        String refreshToken = "old-refresh-token";
        MockHttpServletResponse servletResponse = new MockHttpServletResponse();
        when(authService.refreshToken(refreshToken)).thenReturn("new-access-token");
        when(authService.getNewRefreshToken(refreshToken)).thenThrow(new IllegalArgumentException("Refresh token expired"));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> authController.refreshToken(refreshToken, servletResponse));

        assertEquals(HttpStatus.UNAUTHORIZED, error.getStatusCode());
        assertEquals("Refresh token expired", error.getReason());
        assertNull(servletResponse.getHeader(HttpHeaders.SET_COOKIE));
        verify(authService).getNewRefreshToken(refreshToken);
    }
}
