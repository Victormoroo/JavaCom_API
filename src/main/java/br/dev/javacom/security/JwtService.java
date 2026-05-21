package br.dev.javacom.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class JwtService {

    private final JwtProperties properties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
        byte[] decoded = Base64.getDecoder().decode(properties.secret());
        this.signingKey = Keys.hmacShaKeyFor(decoded);
    }

    public TokenPair generate(String username, String role) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.expirationMinutes(), ChronoUnit.MINUTES);

        String token = Jwts.builder()
                .subject(username)
                .issuer(properties.issuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .claim("role", role)
                .signWith(signingKey)
                .compact();

        LocalDateTime expirationLdt = LocalDateTime.ofInstant(expiresAt, ZoneId.systemDefault());
        return new TokenPair(token, expirationLdt);
    }

    public Optional<Claims> parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(properties.issuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(claims);
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("JWT inválido: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    public record TokenPair(String token, LocalDateTime expiresAt) {}
}
