package com.cen4010.gamescoretracker.util;

import com.cen4010.gamescoretracker.api.user.database.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("my_secret_key_which_is_long_enough".getBytes());
    private static final long EXPIRATION = 1000 * 60 * 60 * 24; // 24 hours

    // --- Token generation ---
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername()) // use username as principal
                .claim("userId", user.getUserId().toString())
                .claim("role", user.getRole().name())
                .claim("group_code", user.getGroupCode())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    // --- Extract username (for SecurityContext) ---
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // --- Extract userId if needed ---
    public UUID extractUserId(String token) {
        String id = (String) getClaims(token).get("userId");
        return UUID.fromString(id);
    }

    // --- Validate token ---
    public boolean isTokenValid(String token, User user) {
        String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    // --- Build Authentication for Spring Security ---
    public Authentication getAuthentication(String token, User user) {
        // You can add roles/authorities if needed
        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }
}
