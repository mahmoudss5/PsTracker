package com.TrainingTracker.TraingingTracker.BusinessLogic.ImpServiceLayer.Auth;

import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.AuthService;
import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.CfService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth.AuthResponse;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth.SignInDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth.SignUpDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.RefreshToken;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.Types.Role;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.RefershTokenRepositroy;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.UserRepository;
import com.TrainingTracker.TraingingTracker.Security.User.SecurityUser;
import com.TrainingTracker.TraingingTracker.Security.jwt.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImpAuthService implements AuthService {

    private final UserRepository userRepository;
    private final RefershTokenRepositroy refershTokenRepositroy;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CfService cfService;
    @Override
    public AuthResponse signIn(SignInDto signDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            signDto.email(),
                            signDto.password()
                    )
            );
        } catch (AuthenticationException exception) {
            log.warn("Failed sign-in attempt for email {}", signDto.email());
            throw exception;
        }

        User user = userRepository.findByEmail(signDto.email())
                .orElseThrow(() -> new IllegalStateException("Authenticated user was not found"));

        AuthResponse authResponse = generateAuthResponse(user);
        log.info("User {} signed in successfully", user.getEmail());
        return authResponse;
    }


    @Override
    @Transactional
    @CacheEvict(value = "trainees",allEntries = true,condition = "#dto.isCoach() == false")
    public AuthResponse signUp(SignUpDto dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }
        if(!cfService.checkIfUserCfAccountExist(dto.codeforcesHandle())){
            throw new IllegalArgumentException("Codeforces account does not exist");
        }

        Long rating = cfService.getUserRating(dto.codeforcesHandle());

        User user = User.builder()
                .username(dto.userName())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .role(dto.isCoach() ? Role.Coach : Role.Trainee)
                .rate(rating)
                .codeforcesHandle(dto.codeforcesHandle())
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        AuthResponse authResponse = generateAuthResponse(savedUser);
        log.info("User {} signed up successfully", savedUser.getEmail());
        return authResponse;
    }

    @Override
    @Transactional
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Authentication required");
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user was not found"));

        refershTokenRepositroy.deleteByUserId(user.getId());
        SecurityContextHolder.clearContext();
        log.info("User {} logged out successfully", user.getEmail());
    }

    @Override
    @Transactional
    public String refreshToken(String RefreshToken) {
        RefreshToken storedRefreshToken = jwtService.getValidRefreshToken(RefreshToken);
        User user = storedRefreshToken.getUser();
        UserDetails userDetails = new SecurityUser(user);
        return jwtService.generateToken(userDetails);
    }

    @Override
    @Transactional
    public String getNewRefreshToken(String RefreshToken) {
        RefreshToken storedRefreshToken = jwtService.getValidRefreshToken(RefreshToken);
        Long userId = storedRefreshToken.getUser().getId();

        jwtService.deleteByUserId(userId);
        return jwtService.generateRefreshToken(userId);
    }

    private AuthResponse generateAuthResponse(User user) {

        SecurityUser securityUser = new SecurityUser(user);
        String accessToken = jwtService.generateToken(securityUser);
        String refreshToken = jwtService.generateRefreshToken(user.getId());
        boolean isCoach = user.getRole() == Role.Coach;
        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getId(),
                user.getUsername(),
                isCoach
        );
    }

}
