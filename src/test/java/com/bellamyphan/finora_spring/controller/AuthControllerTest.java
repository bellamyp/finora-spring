package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.constant.RoleEnum;
import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.UserRepository;
import com.bellamyphan.finora_spring.service.EmailService;
import com.bellamyphan.finora_spring.service.JwtService;
import com.bellamyphan.finora_spring.service.OtpService;
import com.bellamyphan.finora_spring.service.PasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private OtpService otpService;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthController authController;

    private static final String USER_ID = "AbC123xYz9";   // 10-char nanoID

    // ------------------ /login tests ------------------
    @Test
    void testLoginSuccess() {
        String email = "test@example.com";
        String password = "password123";

        Role role = new Role(RoleEnum.ROLE_USER);

        User user = new User();
        user.setId(USER_ID);
        user.setEmail(email);
        user.setPassword("hashedPassword");
        user.setRole(role);

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(user));
        when(passwordService.matches(password, "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken(email, USER_ID, RoleEnum.ROLE_USER)).thenReturn("mocked-jwt-token");

        ResponseEntity<?> response = authController.login(email, password);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals(true, body.get("success"));
        assertEquals("mocked-jwt-token", body.get("token"));
    }

    @Test
    void testLoginUserNotFound() {
        String email = "missing@example.com";

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.login(email, "any");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email or password", response.getBody());
    }

    @Test
    void testLoginWrongPassword() {
        String email = "test@example.com";

        User user = new User();
        user.setId(USER_ID);
        user.setEmail(email);
        user.setPassword("correctHashed");

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(user));
        when(passwordService.matches("wrongPass", "correctHashed")).thenReturn(false);

        ResponseEntity<?> response = authController.login(email, "wrongPass");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email or password", response.getBody());
    }

    // ------------------ /login/otp/request tests ------------------
    @Test
    void testRequestOtpSuccess() {
        String email = "test@example.com";

        User user = new User();
        user.setId(USER_ID);
        user.setEmail(email);

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Optional.of(user));
        when(otpService.generateOtp()).thenReturn("123456");

        ResponseEntity<?> response = authController.requestOtp(email);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals("OTP sent to your email", body.get("message"));

        verify(otpService).saveOtp(eq(email), eq("123456"), any(LocalDateTime.class));
        verify(emailService).sendOtpEmail(email, "123456");
    }

    @Test
    void testRequestOtpUserNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.requestOtp("missing@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        assertEquals("User not found", body.get("message"));
    }

    // ------------------ /login/otp/verify tests ------------------
    @Test
    void testVerifyOtpSuccess() {
        String email = "test@example.com";
        String otp = "123456";

        Role role = new Role(RoleEnum.ROLE_USER);

        User user = new User();
        user.setId(USER_ID);
        user.setEmail(email);
        user.setRole(role);

        OtpService.OtpEntry entry =
                new OtpService.OtpEntry(otp, LocalDateTime.now().plusMinutes(5));

        when(otpService.getOtp(email)).thenReturn(entry);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(email, USER_ID, RoleEnum.ROLE_USER)).thenReturn("mocked-jwt");

        ResponseEntity<?> response = authController.verifyOtp(email, otp);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals("mocked-jwt", body.get("token"));

        verify(otpService).clearOtp(email);
    }

    @Test
    void testVerifyOtpInvalidOrExpired() {
        when(otpService.getOtp("test@example.com")).thenReturn(null);

        ResponseEntity<?> response = authController.verifyOtp("test@example.com", "000000");

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        assertEquals("Invalid or expired OTP", body.get("message"));

        verify(otpService).clearOtp("test@example.com");
    }

    @Test
    void testVerifyOtpUserNotFound() {
        String email = "missing@example.com";
        String otp = "123456";

        OtpService.OtpEntry entry =
                new OtpService.OtpEntry(otp, LocalDateTime.now().plusMinutes(5));

        when(otpService.getOtp(email)).thenReturn(entry);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.verifyOtp(email, otp);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        assertEquals("User not found", body.get("message"));

        verify(otpService).clearOtp(email);
    }
}
