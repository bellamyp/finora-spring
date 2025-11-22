package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.UserRepository;
import com.bellamyphan.finora_spring.service.EmailService;
import com.bellamyphan.finora_spring.service.JwtService;
import com.bellamyphan.finora_spring.service.OtpService;
import com.bellamyphan.finora_spring.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final OtpService otpService;
    private final JwtService jwtService;
    private final EmailService emailService;

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

        // Generate JWT token with userId, email, role
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole().getName() // RoleEnum
        );


        return ResponseEntity.ok(Map.of(
                "success", true,
                "token", token
        ));
    }

    @PostMapping("/login/otp/request")
    public ResponseEntity<?> requestOtp(@RequestParam String email) {

        Optional<User> userOpt = userRepository.findByEmail(email.toLowerCase());
        if (userOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "User not found"
            ));
        }

        String otp = otpService.generateOtp();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);
        otpService.saveOtp(email, otp, expiry);

        emailService.sendOtpEmail(email, otp);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "OTP sent to your email"
        ));
    }

    @PostMapping("/login/otp/verify")
    public ResponseEntity<?> verifyOtp(@RequestParam String email,
                                       @RequestParam String otp) {
        String lowerCaseEmail = email.toLowerCase();
        OtpService.OtpEntry entry = otpService.getOtp(lowerCaseEmail);

        // Check existence, expiry, and match
        if (entry == null || !entry.otp().equals(otp) || LocalDateTime.now().isAfter(entry.expiry())) {
            otpService.clearOtp(lowerCaseEmail); // cleanup expired or invalid OTP
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "success", false,
                    "message", "Invalid or expired OTP"
            ));
        }

        otpService.clearOtp(lowerCaseEmail); // cleanup after successful verification

        // Safely get user or return not found
        Optional<User> userOpt = userRepository.findByEmail(lowerCaseEmail);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                    "success", false,
                    "message", "User not found"
            ));
        }

        User user = userOpt.get();
        // Generate JWT token for verified OTP
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole().getName() // RoleEnum
        );

        return ResponseEntity.ok(Map.of(
                "success", true,
                "token", token
        ));
    }
}
