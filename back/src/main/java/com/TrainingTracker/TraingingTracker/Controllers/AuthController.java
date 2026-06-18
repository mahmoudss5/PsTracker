package com.TrainingTracker.TraingingTracker.Controllers;


import com.TrainingTracker.TraingingTracker.BusinessLogic.InterfacesServiceLayer.AuthService;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth.AuthResponse;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth.SignInDto;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Dto.Auth.SignUpDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;


  @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody SignInDto dto, HttpServletResponse response) {
      AuthResponse authResponse=authService.signIn(dto);
      ResponseCookie cookie=ResponseCookie.from("refreshToken",authResponse.RefershToken())
              .httpOnly(true)
              .secure(true)
              .path("/api/auth")
              .maxAge(Duration.ofDays(7))
              .sameSite("Strict")
              .build();
      response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

      return ResponseEntity.ok(authResponse);

  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@RequestBody SignUpDto dto, HttpServletResponse response) {
      AuthResponse authResponse=authService.signUp(dto);
      ResponseCookie cookie=ResponseCookie.from("refreshToken",authResponse.RefershToken())
              .httpOnly(true)
              .secure(true)
              .path("/api/auth")
              .maxAge(Duration.ofDays(7))
              .sameSite("Strict")
              .build();
      response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

      return ResponseEntity.ok(authResponse);

  }

  @PostMapping("/logout")
  public void logout(   @CookieValue(name = "refreshToken", required = false) String refreshToken,
                        HttpServletResponse response) {
      authService.logout();
      ResponseCookie deleteCookie=ResponseCookie.from("refreshToken",refreshToken)
              .httpOnly(true)
              .secure(true)
              .path("/api/auth")
              .maxAge(Duration.ZERO)
              .sameSite("Strict")
              .build();
      response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
  }

  @GetMapping("/refresh")
  public ResponseEntity<String> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
      if (refreshToken == null || refreshToken.isBlank()) {
        return ResponseEntity.badRequest().build();
      }

      String accessToken;
      String newRefreshToken;
      try {
          accessToken = authService.refreshToken(refreshToken);
          newRefreshToken = authService.getNewRefreshToken(refreshToken);
      } catch (IllegalArgumentException exception) {
          throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, exception.getMessage());
      }
      ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
              .httpOnly(true)
              .secure(true)
              .path("/api/auth")
              .maxAge(Duration.ofDays(7))
              .sameSite("Strict")
              .build();
      response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

      return ResponseEntity.ok(accessToken);
  }

}
