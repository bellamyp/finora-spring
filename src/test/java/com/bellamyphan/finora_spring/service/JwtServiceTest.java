package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.RoleEnum;
import com.bellamyphan.finora_spring.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private JwtService jwtService;

    private User user;
    private final String userId = "AbC123xYz9";
    private final String email = "test@example.com";
    private Key key;

    @BeforeEach
    void setup() throws Exception {
        // Inject secret manually (>=32 chars for HS256)
        var secretField = JwtService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        String secret = "mysecretmysecretmysecretmysecret";
        secretField.set(jwtService, secret);

        user = new User();
        user.setId(userId);
        user.setEmail(email);

        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ---------------- Generate Token ----------------
    @Test
    void testGenerateToken() {
        String token = jwtService.generateToken(email, userId, RoleEnum.ROLE_USER);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length); // JWT format: header.payload.signature
    }

    // ---------------- Validate Token ----------------
    @Test
    void testValidateTokenSuccess() {
        String token = jwtService.generateToken(email, userId, RoleEnum.ROLE_USER);

        when(userService.findById(userId)).thenReturn(Optional.of(user));

        String result = jwtService.validateTokenAndGetUserId(token);

        assertEquals(userId, result);
        verify(userService, times(1)).findById(userId);
    }

    @Test
    void testValidateTokenInvalidSignature() {
        // Generate token with a different secret key to simulate invalid signature
        Key wrongKey = Keys.hmacShaKeyFor("wrongsecretwrongsecretwrongsecret1".getBytes(StandardCharsets.UTF_8));
        String badToken = Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .claim("userId", userId)
                .claim("role", RoleEnum.ROLE_USER.name())
                .signWith(wrongKey, SignatureAlgorithm.HS256)
                .compact();

        String result = jwtService.validateTokenAndGetUserId(badToken);

        assertNull(result);
    }

    @Test
    void testValidateTokenUserNotFound() {
        String token = jwtService.generateToken(email, userId, RoleEnum.ROLE_USER);

        when(userService.findById(userId)).thenReturn(Optional.empty());

        String result = jwtService.validateTokenAndGetUserId(token);

        assertNull(result);
        verify(userService, times(1)).findById(userId);
    }

    @Test
    void testValidateTokenExpired() {
        // Token already expired
        String expiredToken = Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) // past
                .claim("userId", userId)
                .claim("role", RoleEnum.ROLE_USER.name())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // No need to stub userService; method returns null on expiration
        String result = jwtService.validateTokenAndGetUserId(expiredToken);

        assertNull(result);
    }

    @Test
    void testValidateTokenMalformed() {
        String badToken = "this.is.not.a.jwt";

        String result = jwtService.validateTokenAndGetUserId(badToken);

        assertNull(result);
    }
}
