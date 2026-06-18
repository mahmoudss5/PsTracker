package com.TrainingTracker.TraingingTracker.Security.jwt;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.RefreshToken;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Entites.User;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.RefershTokenRepositroy;
import com.TrainingTracker.TraingingTracker.DataAccessLayer.Repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access-expiration}")
    private Long expiration;

    @Value("${jwt.refresh-expiration}")
    private Long refresh_expiration;

    private final RefershTokenRepositroy refreshTokenRepository;
    private final UserRepository userRepository;


    public String generateRefreshToken(Long userId) {

        User user = userRepository.findById(userId).orElseThrow();
        String rawRefreshToken = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .hased_refresh_token(hashRefreshToken(rawRefreshToken))
                .createdAt(Instant.now())
                .expiryDate(Instant.now().plusMillis(refresh_expiration))
                .valid(true)
                .build();
        refreshTokenRepository.save(refreshToken);
        return rawRefreshToken;
    }

    public RefreshToken getValidRefreshToken(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token is required");
        }

        RefreshToken token = refreshTokenRepository
                .findByHasedRefreshToken(hashRefreshToken(rawRefreshToken))
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        if (!Boolean.TRUE.equals(token.getValid())
                || !token.getExpiryDate().isAfter(Instant.now())) {
            throw new IllegalArgumentException("Refresh token is not valid");
        }

        return token;
    }

    private String hashRefreshToken(String rawRefreshToken) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(rawRefreshToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }




   @Transactional
   public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
   }

    private HashMap<String, Object> getExtraClaims(User user) {

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole());
        claims.put("userName", user.getUsername());
        return claims;
    }

    public String generateToken(UserDetails userDetails) {

        User user =userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with username: " + userDetails.getUsername()));
        return generateToken(getExtraClaims(user), userDetails);
    }


    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, java.util.function.Function<io.jsonwebtoken.Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = secretKey.getBytes();
        return io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);
    }

}
