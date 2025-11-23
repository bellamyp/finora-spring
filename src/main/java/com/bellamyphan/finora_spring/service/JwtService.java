package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.RoleEnum;
import com.bellamyphan.finora_spring.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserService userService;

    // 1 hour expiration token
    private static final long EXPIRATION_MS = 1000 * 60 * 60;

    @Value("${jwt.secret}")
    private String secret;
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Generate JWT
    public String generateToken(String email, String userId, RoleEnum role) {
        Map<String, Object> claims = Map.of(
                "userId", userId,
                "role", role.name()
        );
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Verify token and return userId if valid
    public String validateTokenAndGetUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String userId = claims.get("userId", String.class);

            // Check if user exists in DB
            Optional<User> userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                return null; // invalid token: user not found
            }

            // Optionally: check expiration manually
            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                return null; // token expired
            }

            return userId;

        } catch (Exception e) {
            return null; // invalid token
        }
    }
}
