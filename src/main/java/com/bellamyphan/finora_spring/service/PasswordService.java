package com.bellamyphan.finora_spring.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Hash a raw password using BCrypt
     */
    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * Validate provided password against stored hash
     */
    public boolean matches(String rawPassword, String hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword);
    }
}
