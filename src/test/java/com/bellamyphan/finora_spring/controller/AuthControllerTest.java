package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthController authController;

    @Test
    void testLoginSuccess() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<String> response = authController.login(email, password);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login successful", response.getBody());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testLoginUserNotFound() {
        // Arrange
        String email = "notfound@example.com";
        String password = "anyPassword";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<String> response = authController.login(email, password);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email or password", response.getBody());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testLoginWrongPassword() {
        // Arrange
        String email = "test@example.com";
        String password = "wrongPassword";
        User user = new User();
        user.setEmail(email);
        user.setPassword("correctPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<String> response = authController.login(email, password);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid email or password", response.getBody());
        verify(userRepository, times(1)).findByEmail(email);
    }
}
