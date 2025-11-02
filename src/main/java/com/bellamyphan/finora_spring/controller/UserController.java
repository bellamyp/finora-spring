package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.RoleRepository;
import com.bellamyphan.finora_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users") // base path for all user endpoints
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    // GET /api/users - fetch all users
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // POST /api/users - create a new user
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody User user) {
        // Optional: check if email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists");
        }

        // Optional: validate role exists
        Optional<Role> roleOpt = roleRepository.findById(user.getRole().getId());
        if (roleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid role ID");
        }

        user.setRole(roleOpt.get());

        // Save user
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
    }
}