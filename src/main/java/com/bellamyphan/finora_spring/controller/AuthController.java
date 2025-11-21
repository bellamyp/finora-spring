package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.UserRepository;
import com.bellamyphan.finora_spring.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email,
                                   @RequestParam String password) {
        Optional<User> userOpt = userRepository.findByEmail(email.toLowerCase());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        User user = userOpt.get();

        // Use PasswordService to check hashed password
        if (!passwordService.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        // Successful login → return email + role as JSON
        return ResponseEntity.ok(new LoginResponse(user.getEmail(), user.getRole().toString()));
    }

    // Create a small DTO for login response
    record LoginResponse(String email, String role) { }
}
