package com.ecommerce.sbecom.security;
import com.ecommerce.sbecom.model.Role;
import com.ecommerce.sbecom.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
@Slf4j
public class JwtService {
    private final SecretKey key;
    private final long accessTtlSeconds;
    private final long refreshTtlSeconds;
    private String issuer;

    public JwtService(@Value("${security.jwt.issuer}") String issuer,
                      @Value("${security.jwt.secret-key}") String secret,
                      @Value("${security.jwt.access-ttl-seconds}") long accessTtlSeconds,
                      @Value("${security.jwt.refresh-ttl-seconds}") long refreshTtlSeconds) {

        if (secret == null || secret.length() < 64) {
            throw new IllegalArgumentException("Secret key must be at least 64 characters long");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        this.issuer = issuer;

        this.accessTtlSeconds = accessTtlSeconds;
        this.refreshTtlSeconds = refreshTtlSeconds;
    }

    //generate token
    public String generateToken(User user) {
        log.debug("Generating access token for user: {}", user.getEmail());
        Instant now = Instant.now();
        List<String> roles = user.getRoles().stream().map(Role::getName).toList();
        log.debug("User roles: {}", roles);
        log.debug("Token will expire in {} seconds", accessTtlSeconds);

        String token = Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))

                .expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
                .claims(Map.of(
                        "email", user.getEmail(),
                        "roles", roles,
                        "type", "access"
                ))
                .signWith(key, Jwts.SIG.HS256)

                .compact();
        log.debug("Access token generated successfully for user: {}", user.getEmail());
        log.trace("Generated token: {}", token); // Only in trace level for security
        return token;
    }

    //generate refresh token
    public String generateRefreshToken(User user, String jti) {
        log.debug("Generating refresh token for user: {}", user.getEmail());
        Instant now = Instant.now();
        log.debug("Token will expire in {} seconds", refreshTtlSeconds);
        String token = Jwts.builder()
                .id(jti)
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTtlSeconds)))
                .claims(Map.of(
                        "email", user.getEmail(),
                        "type", "refresh"
                ))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
        log.debug("Refresh token generated successfully for user: {}", user.getEmail());
        log.trace("Generated token: {}", token); // Only in trace level for security
        return token;
    }

    // Parse and validate token
    public Jws<Claims> parse(String token) {
        try {
            log.debug("Parsing JWT token");

            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            log.debug("Token parsed successfully. Subject: {}",
                    claimsJws.getPayload().getSubject());

            return claimsJws;

        } catch (JwtException e) {
            log.error("Failed to parse JWT token: {}", e.getMessage());
            throw e;
        }
    }

    // Extract claims from token
    public Claims extractClaims(String token) {
        return parse(token).getPayload();
    }

    // Validate token
    public boolean validateToken(String token) {
        try {
            parse(token);
            log.debug("Token validation successful");
            return true;
        } catch (JwtException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // Get user ID from token
    public UUID getUserIdFromToken(String token) {
        Claims claims = parse(token).getPayload();
        return UUID.fromString(claims.getSubject());
    }

    public String getJti(String token) {
        return parse(token).getPayload().getId();
    }

    // Get email from token
    public String getEmailFromToken(String token) {
        Claims claims = extractClaims(token);
        return claims.get("email", String.class);
    }

    // Get roles from token
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = extractClaims(token);
        return (List<String>) claims.get("roles");
    }

    // Get token type from token
    public String getTokenTypeFromToken(String token) {
        Claims claims = extractClaims(token);
        return claims.get("type", String.class);
    }

    // Check if token is expired
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    public boolean isAccessToken(String token) {
        Claims payload = parse(token).getPayload();
        return "access".equals(payload.get("type"));

    }

    public boolean isRefreshToken(String token) {
        Claims payload = parse(token).getPayload();
        return "refresh".equals(payload.get("type"));

    }
}
