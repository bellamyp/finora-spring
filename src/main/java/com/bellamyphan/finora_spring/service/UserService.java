package com.bellamyphan.finora_spring.service;

import com.bellamyphan.finora_spring.constant.RoleEnum;
import com.bellamyphan.finora_spring.dto.UserRequestDto;
import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.RoleRepository;
import com.bellamyphan.finora_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final NanoIdService nanoIdService;
    private final PasswordService passwordService;

    public User createUser(UserRequestDto userDto) {

        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        // Check if userDto has password before hashing
        if (userDto.getPassword() == null || userDto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        // Hash the password
        user.setPassword(passwordService.hash(userDto.getPassword()));

        // Fetch existing role from DB
        Role roleUser = roleRepository.findByName(RoleEnum.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
        user.setRole(roleUser);

        // Save new userDto to the DB
        for (int i = 0; i < 10; i++) {
            try {
                user.setId(nanoIdService.generate());
                return userRepository.save(user);
            } catch (DataIntegrityViolationException ignored) {
                // retry
            }
        }
        throw new RuntimeException("Failed to generate unique NanoID after 10 attempts");
    }
}
