package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.dto.UserDto;
import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.entity.RoleEnum;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.RoleRepository;
import com.bellamyphan.finora_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users") // base path for all user endpoints
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

//    // GET /api/users - fetch all users
//    @GetMapping
//    public List<UserDto> getAllUsers() {
//        return userRepository.findAll()
//                .stream()
//                .map(user -> new UserDto(
//                        user.getId(),
//                        user.getName(),
//                        user.getEmail(),
//                        user.getRole() != null ? user.getRole().getName() : "ROLE_USER"
//                ))
//                .collect(Collectors.toList());
//    }

//    // POST /api/users - create a new user
//    @PostMapping
//    public ResponseEntity<String> createUser(@RequestBody User user) {
//        // Optional: check if email already exists
//        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body("Email already exists");
//        }
//
//        // ✅ Assign default role automatically if none provided
//        if (user.getRole() == null) {
//            Optional<Role> userRole = roleRepository.findByName(RoleEnum.ROLE_USER.toString());
//            if (userRole.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body("ROLE_USER role not found in DB");
//            }
//            user.setRole(userRole.get());
//        } else {
//            // Validate provided role exists
//            Optional<Role> roleOpt = roleRepository.findById(user.getRole().getId());
//            if (roleOpt.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body("Invalid role ID");
//            }
//            user.setRole(roleOpt.get());
//        }
//
//        // Save user
//        userRepository.save(user);
//        return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
//    }
}