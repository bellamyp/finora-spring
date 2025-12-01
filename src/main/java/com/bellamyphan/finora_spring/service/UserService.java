package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.RoleEnum;
import com.bellamyphan.finora_spring.dto.UserRequestDto;
import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.RoleRepository;
import com.bellamyphan.finora_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final NanoIdService nanoIdService;
    private final PasswordService passwordService;

    @Transactional
    public User createUser(UserRequestDto userDto) {

        // 1. Validate email
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        // 2. Check if email already exists
        if (userRepository.existsByEmailIgnoreCase(userDto.getEmail().toLowerCase())) {
            throw new IllegalArgumentException("Email already exists: " + userDto.getEmail());
        }

        // 3. Validate password
        if (userDto.getPassword() == null || userDto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // 4. Convert role string to RoleEnum, default to ROLE_USER
        RoleEnum roleEnum;
        try {
            if (userDto.getRole() == null || userDto.getRole().isBlank()) {
                roleEnum = RoleEnum.ROLE_USER;
            } else {
                roleEnum = RoleEnum.valueOf(userDto.getRole());
            }
        } catch (IllegalArgumentException ex) {
            // Invalid role string -> default to ROLE_USER
            roleEnum = RoleEnum.ROLE_USER;
        }

        // 5. Fetch role entity from DB, default to ROLE_USER if not found
        Role role = roleRepository.findByName(roleEnum)
                .orElseGet(() -> roleRepository.findByName(RoleEnum.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("ROLE_USER not found in DB")));

        // 6. Create User entity
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail().toLowerCase());
        user.setPassword(passwordService.hash(userDto.getPassword()));
        user.setRole(role);

        // 7. Generate unique NanoID with retry
        String newId = nanoIdService.generateUniqueId(userRepository);
        user.setId(newId);
        return userRepository.save(user);
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
