package com.TrainingTracker.TraingingTracker.unit.service;

import com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Auth.ImpAuthService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.CfService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth.AuthResponse;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth.SignInDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth.SignUpDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Codeforces.CodeforcesUserInfo;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.RefreshToken;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Types.Role;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.RefershTokenRepositroy;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.UserRepository;
import com.TrainingTracker.TraingingTracker.ExceptionHandling.ErrosEntites.CfUserHandlerError;
import com.TrainingTracker.TraingingTracker.Security.jwt.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImpAuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefershTokenRepositroy refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CfService cfService;

    private ImpAuthService authService;

    @BeforeEach
    void setUp() {
        authService = new ImpAuthService(userRepository, refreshTokenRepository,
                passwordEncoder, jwtService, authenticationManager, cfService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void signInAuthenticatesCredentialsAndReturnsTokens() {
        User user = user(7L, Role.Coach);
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("access");
        when(jwtService.generateRefreshToken(7L)).thenReturn("refresh");

        AuthResponse result = authService.signIn(new SignInDto(user.getEmail(), "secret"));

        ArgumentCaptor<Authentication> credentials = ArgumentCaptor.forClass(Authentication.class);
        verify(authenticationManager).authenticate(credentials.capture());
        assertEquals(user.getEmail(), credentials.getValue().getPrincipal());
        assertEquals("secret", credentials.getValue().getCredentials());
        assertEquals("access", result.token());
        assertEquals("refresh", result.RefershToken());
        assertEquals(7L, result.userId());
        assertEquals("trainee", result.userName());
        assertTrue(result.isCoach());
    }

    @Test
    void signInConcealsAuthenticationFailureAndDoesNotIssueTokens() {
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("specific reason"));

        BadCredentialsException error = assertThrows(BadCredentialsException.class,
                () -> authService.signIn(new SignInDto("user@example.com", "wrong")));

        assertEquals("Invalid email or password", error.getMessage());
        verifyNoInteractions(userRepository, jwtService);
    }

    @Test
    void signInFailsWhenAuthenticatedUserCannotBeLoaded() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> authService.signIn(new SignInDto("user@example.com", "secret")));

        assertEquals("Authenticated user was not found", error.getMessage());
        verifyNoInteractions(jwtService);
    }

    @Test
    void traineeSignUpStoresEncodedPasswordAndCodeforcesProfile() {
        SignUpDto dto = new SignUpDto("trainee", "user@example.com", "cf_user", "secret123", false);
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(cfService.getUserInfo("cf_user"))
                .thenReturn(new CodeforcesUserInfo(1200L, 1300L, "Newbie", "Pupil"));
        when(passwordEncoder.encode("secret123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(7L);
            return saved;
        });
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("access");
        when(jwtService.generateRefreshToken(7L)).thenReturn("refresh");

        AuthResponse result = authService.signUp(dto);

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertEquals("encoded", saved.getValue().getPassword());
        assertEquals(Role.Trainee, saved.getValue().getRole());
        assertEquals("cf_user", saved.getValue().getCodeforcesHandle());
        assertEquals(1200L, saved.getValue().getRate());
        assertEquals(1300L, saved.getValue().getMaxRate());
        assertEquals("Newbie", saved.getValue().getRank());
        assertEquals("Pupil", saved.getValue().getMaxRank());
        assertNotNull(saved.getValue().getCreatedAt());
        assertEquals("access", result.token());
        assertEquals("refresh", result.RefershToken());
        assertEquals(false, result.isCoach());
    }

    @Test
    void coachSignUpWorksWhenCodeforcesHasNoRating() {
        SignUpDto dto = new SignUpDto("coach", "user@example.com", "cf_coach", "secret123", true);
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(7L);
            return saved;
        });
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("access");
        when(jwtService.generateRefreshToken(7L)).thenReturn("refresh");

        AuthResponse result = authService.signUp(dto);

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(cfService).getUserInfo("cf_coach");
        verify(userRepository).save(saved.capture());
        assertEquals(Role.Coach, saved.getValue().getRole());
        assertNull(saved.getValue().getRate());
        assertTrue(result.isCoach());
    }

    @Test
    void signUpRejectsDuplicateEmailBeforeCallingCodeforces() {
        SignUpDto dto = new SignUpDto("trainee", "user@example.com", "cf_user", "secret123", false);
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(user(7L, Role.Trainee)));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> authService.signUp(dto));

        assertEquals("Email is already registered", error.getMessage());
        verifyNoInteractions(cfService, passwordEncoder, jwtService);
        verify(userRepository, never()).save(any());
    }

    @Test
    void signUpRejectsUnknownCodeforcesAccountBeforeSaving() {
        SignUpDto dto = new SignUpDto("trainee", "user@example.com", "missing", "secret123", false);
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(cfService.getUserInfo("missing")).thenThrow(new CfUserHandlerError("not found"));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> authService.signUp(dto));

        assertEquals("Codeforces account does not exist", error.getMessage());
        assertInstanceOf(CfUserHandlerError.class, error.getCause());
        verify(userRepository, never()).save(any());
    }

    @Test
    void logoutDeletesRefreshTokensAndClearsSecurityContext() {
        authenticateAs("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user(7L, Role.Trainee)));

        authService.logout();

        verify(refreshTokenRepository).deleteByUserId(7L);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void logoutWithoutAuthenticationDoesNothing() {
        authService.logout();

        verifyNoInteractions(userRepository, refreshTokenRepository);
    }

    @Test
    void logoutWithUnknownUserDoesNotDeleteTokens() {
        authenticateAs("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        authService.logout();

        verifyNoInteractions(refreshTokenRepository);
    }

    @Test
    void refreshTokenIssuesAccessTokenForStoredUser() {
        User user = user(7L, Role.Trainee);
        when(jwtService.getValidRefreshToken("old")).thenReturn(RefreshToken.builder().user(user).build());
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("new-access");

        String result = authService.refreshToken("old");

        ArgumentCaptor<UserDetails> details = ArgumentCaptor.forClass(UserDetails.class);
        verify(jwtService).generateToken(details.capture());
        assertEquals("new-access", result);
        assertEquals(user.getEmail(), details.getValue().getUsername());
    }

    @Test
    void invalidRefreshTokenDoesNotIssueAccessToken() {
        when(jwtService.getValidRefreshToken("invalid")).thenThrow(new IllegalArgumentException("invalid"));

        assertThrows(IllegalArgumentException.class, () -> authService.refreshToken("invalid"));

        verify(jwtService, never()).generateToken(any(UserDetails.class));
    }

    @Test
    void refreshTokenRotationRevokesOldTokenBeforeIssuingNewOne() {
        when(jwtService.getValidRefreshToken("old"))
                .thenReturn(RefreshToken.builder().user(user(7L, Role.Trainee)).build());
        when(jwtService.generateRefreshToken(7L)).thenReturn("new-refresh");

        assertEquals("new-refresh", authService.getNewRefreshToken("old"));

        InOrder order = inOrder(jwtService);
        order.verify(jwtService).getValidRefreshToken("old");
        order.verify(jwtService).deleteByUserId(7L);
        order.verify(jwtService).generateRefreshToken(7L);
    }

    private User user(Long id, Role role) {
        return User.builder().id(id).email("user@example.com").username("trainee").role(role).build();
    }

    private void authenticateAs(String email) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, "ignored", List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
