package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.constant.RoleEnum;
import com.bellamyphan.finora_spring.dto.UserDto;
import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.UserRepository;
import com.bellamyphan.finora_spring.service.EmailService;
import com.bellamyphan.finora_spring.service.OtpService;
import com.bellamyphan.finora_spring.service.PasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    @InjectMocks
    private AuthController authController;

    // ------------------ /login tests ------------------
    @Test
    void testLoginSuccess() {
        String email = "test@example.com";
        String password = "password123";

        Role role = new Role(RoleEnum.ROLE_USER);

        User user = new User();
        user.setEmail(email);
        user.setPassword("hashedPassword");
        user.setName("Test User");
        user.setRole(role);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordService.matches(password, "hashedPassword")).thenReturn(true);

        ResponseEntity<?> response = authController.login(email, password);

        assertEquals(200, response.getStatusCodeValue());
        assertInstanceOf(UserDto.class, response.getBody());

        UserDto dto = (UserDto) response.getBody();
        assertEquals("Test User", dto.getName());
        assertEquals(email, dto.getEmail());
        assertEquals(role.toString(), dto.getRole());

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testLoginUserNotFound() {
        String email = "notfound@example.com";
        String password = "anyPassword";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.login(email, password);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid email or password", response.getBody());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testLoginWrongPassword() {
        String email = "test@example.com";
        String password = "wrongPassword";

        Role role = new Role(RoleEnum.ROLE_ADMIN);

        User user = new User();
        user.setEmail(email);
        user.setPassword("correctPassword");
        user.setRole(role);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordService.matches(password, "correctPassword")).thenReturn(false);

        ResponseEntity<?> response = authController.login(email, password);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid email or password", response.getBody());
        verify(userRepository, times(1)).findByEmail(email);
    }

    // ------------------ /login/otp/request tests ------------------
    @Test
    void testRequestOtpSuccess() {
        String email = "test@example.com";

        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(otpService.generateOtp()).thenReturn("123456");

        ResponseEntity<?> response = authController.requestOtp(email);

        assertEquals(200, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals("OTP sent to your email", body.get("message"));

        verify(otpService).saveOtp(eq(email), eq("123456"), any(LocalDateTime.class));
        verify(emailService).sendOtpEmail(email, "123456");
    }

    @Test
    void testRequestOtpUserNotFound() {
        String email = "missing@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.requestOtp(email);

        assertEquals(200, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
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
        user.setEmail(email);
        user.setName("Test User");
        user.setRole(role);

        OtpService.OtpEntry entry = new OtpService.OtpEntry(otp, LocalDateTime.now().plusMinutes(5));

        when(otpService.getOtp(email)).thenReturn(entry);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        ResponseEntity<?> response = authController.verifyOtp(email, otp);

        assertEquals(200, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertTrue((Boolean) body.get("success"));
        assertEquals("OTP verified successfully", body.get("message"));
        assertNotNull(body.get("data"));

        verify(otpService).clearOtp(email);
    }

    @Test
    void testVerifyOtpInvalidOrExpired() {
        String email = "test@example.com";
        String otp = "wrongOtp";

        when(otpService.getOtp(email)).thenReturn(null);

        ResponseEntity<?> response = authController.verifyOtp(email, otp);

        assertEquals(200, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertEquals("Invalid or expired OTP", body.get("message"));

        verify(otpService).clearOtp(email);
    }

    @Test
    void testVerifyOtpUserNotFound() {
        String email = "missing@example.com";
        String otp = "123456";

        OtpService.OtpEntry entry = new OtpService.OtpEntry(otp, LocalDateTime.now().plusMinutes(5));

        when(otpService.getOtp(email)).thenReturn(entry);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        ResponseEntity<?> response = authController.verifyOtp(email, otp);

        assertEquals(200, response.getStatusCodeValue());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertFalse((Boolean) body.get("success"));
        assertEquals("User not found", body.get("message"));

        verify(otpService).clearOtp(email);
    }
}
