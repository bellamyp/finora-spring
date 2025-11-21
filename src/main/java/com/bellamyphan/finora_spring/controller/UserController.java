package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.UserDto;
import com.bellamyphan.finora_spring.dto.UserRequestDto;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.UserRepository;
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

    private final UserRepository userRepository;
    private final UserService userService;

    // GET /api/users - fetch all users
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserDto(
                        user.getName(),
                        user.getEmail(),
                        user.getRole().getName().getDisplayName()
                ))
                .collect(Collectors.toList());
    }

    // POST /api/users - create a new user
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequestDto userDto) {

        // Convert incoming email to lowercase for consistency
        String email = (userDto.getEmail() != null) ? userDto.getEmail().toLowerCase().trim() : null;

        // Check if email already exists
        if (email != null && userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists");
        }

        try {
            // Replace email in DTO with normalized value
            userDto.setEmail(email);

            User saved = userService.createUser(userDto);

            // Return DTO instead of entity because entity includes password
            UserDto dto = new UserDto(
                    saved.getName(),
                    saved.getEmail(),
                    saved.getRole().toString()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(dto);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating userDto: " + e.getMessage());
        }
    }
}