package com.campus.EventManagement.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // MUST be at least 32 characters for HS256
    private static final String SECRET =
            "campus-event-management-secret-key-256";

    private static final long EXPIRATION =
            24 * 60 * 60 * 1000; // 1 day

    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public String generateToken(CustomUserDetails user) {

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role",
                        user.getAuthorities()
                                .iterator()
                                .next()
                                .getAuthority())
                .claim("userId", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}


