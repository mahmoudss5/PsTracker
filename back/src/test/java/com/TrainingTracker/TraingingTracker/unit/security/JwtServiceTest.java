package com.TrainingTracker.TraingingTracker.unit.security;

import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.RefreshToken;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Types.Role;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.RefershTokenRepositroy;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.UserRepository;
import com.TrainingTracker.TraingingTracker.Security.User.SecurityUser;
import com.TrainingTracker.TraingingTracker.Security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private RefershTokenRepositroy refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(refreshTokenRepository, userRepository);
        ReflectionTestUtils.setField(jwtService, "secretKey", "test-jwt-secret-key-with-more-than-32-bytes");
        ReflectionTestUtils.setField(jwtService, "expiration", 60_000L);
        ReflectionTestUtils.setField(jwtService, "refresh_expiration", 3_600_000L);
    }

    @Test
    void generatesOpaqueRefreshTokenButStoresOnlyItsHash() {
        User user = user();
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Instant before = Instant.now();

        String rawToken = jwtService.generateRefreshToken(7L);

        ArgumentCaptor<RefreshToken> saved = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(saved.capture());
        UUID.fromString(rawToken);
        assertEquals(user, saved.getValue().getUser());
        assertFalse(rawToken.equals(saved.getValue().getHasedRefreshToken()));
        assertEquals(64, saved.getValue().getHasedRefreshToken().length());
        assertFalse(saved.getValue().getRevoked());
        assertNotNull(saved.getValue().getCreatedAt());
        assertTrue(saved.getValue().getExpiryDate().isAfter(before.plusSeconds(59 * 60)));
    }

    @Test
    void rejectsBlankRefreshTokenBeforeDatabaseLookup() {
        assertThrows(IllegalArgumentException.class, () -> jwtService.getValidRefreshToken("  "));

        verify(refreshTokenRepository, never()).findByHasedRefreshToken(anyString());
    }

    @Test
    void rejectsUnknownRefreshToken() {
        when(refreshTokenRepository.findByHasedRefreshToken(anyString())).thenReturn(Optional.empty());

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> jwtService.getValidRefreshToken("raw-token"));

        assertEquals("Refresh token not found", error.getMessage());
    }

    @Test
    void rejectsRevokedRefreshToken() {
        RefreshToken stored = RefreshToken.builder().revoked(true)
                .expiryDate(Instant.now().plusSeconds(60)).build();
        when(refreshTokenRepository.findByHasedRefreshToken(anyString())).thenReturn(Optional.of(stored));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> jwtService.getValidRefreshToken("raw-token"));

        assertEquals("Refresh token is not valid", error.getMessage());
    }

    @Test
    void rejectsExpiredRefreshToken() {
        RefreshToken stored = RefreshToken.builder().revoked(false)
                .expiryDate(Instant.now().minusSeconds(1)).build();
        when(refreshTokenRepository.findByHasedRefreshToken(anyString())).thenReturn(Optional.of(stored));

        assertThrows(IllegalArgumentException.class, () -> jwtService.getValidRefreshToken("raw-token"));
    }

    @Test
    void acceptsValidRefreshTokenAndLooksItUpByHash() {
        RefreshToken stored = RefreshToken.builder().revoked(false)
                .expiryDate(Instant.now().plusSeconds(60)).build();
        when(refreshTokenRepository.findByHasedRefreshToken(anyString())).thenReturn(Optional.of(stored));

        assertEquals(stored, jwtService.getValidRefreshToken("raw-token"));

        ArgumentCaptor<String> hash = ArgumentCaptor.forClass(String.class);
        verify(refreshTokenRepository).findByHasedRefreshToken(hash.capture());
        assertFalse(hash.getValue().contains("raw-token"));
        assertEquals(64, hash.getValue().length());
    }

    @Test
    void accessTokenContainsUserIdentityAndValidatesForCorrectUser() {
        User user = user();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        String token = jwtService.generateToken(new SecurityUser(user));

        assertEquals(user.getEmail(), jwtService.extractUsername(token));
        assertTrue(jwtService.isTokenValid(token, new SecurityUser(user)));
        assertFalse(jwtService.isTokenValid(token,
                new SecurityUser(User.builder().email("other@example.com").role(Role.Trainee).build())));
    }

    @Test
    void cannotIssueAccessTokenForUnknownUser() {
        User user = user();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> jwtService.generateToken(new SecurityUser(user)));

        assertTrue(error.getMessage().contains(user.getEmail()));
    }

    @Test
    void deleteByUserIdRevokesStoredRefreshTokens() {
        jwtService.deleteByUserId(7L);

        verify(refreshTokenRepository).deleteByUserId(7L);
    }

    private User user() {
        return User.builder().id(7L).email("user@example.com").username("trainee")
                .role(Role.Trainee).build();
    }
}
