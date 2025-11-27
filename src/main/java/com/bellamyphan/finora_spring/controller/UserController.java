package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.UserDto;
import com.bellamyphan.finora_spring.dto.UserRequestDto;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users") // base path for all user endpoints
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /api/users - fetch all users (admin-only - SecurityConfig)
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAll()
                .stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    // POST /api/users - create a new user (public API)
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequestDto userRequest) {

        // Normalize email
        String email = (userRequest.getEmail() != null)
                ? userRequest.getEmail().toLowerCase().trim()
                : null;

        // Check for duplicate email
        if (email != null && userService.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists");
        }

        try {
            userRequest.setEmail(email);

            // Convert DTO → Entity and save
            User savedUser = userService.createUser(userRequest);

            // Convert saved entity → DTO
            UserDto responseDto = UserDto.fromEntity(savedUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating user: " + e.getMessage());
        }
    }
}
