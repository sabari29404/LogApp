package com.example.Success_Service.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class JwtUtil {

    private final SecretKey key;

    public JwtUtil(@Value("${app.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Map<String, Object> extractUserInfo(String token) {
        Claims claims = extractAllClaims(token);
        return Map.of(
            "subject",  claims.getSubject() != null ? claims.getSubject() : "",
            "name",     claims.getOrDefault("name",     claims.getSubject()) != null ? claims.get("name", String.class) : claims.getSubject(),
            "provider", claims.getOrDefault("provider", "local") != null ? claims.get("provider", String.class) : "local"
        );
    }
}
