package com.bellamyphan.finora_spring.dto;

import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class UserDto {
    private String id;
    private String name;
    private String email;
    private String password;
    private String role;

    // ==========================
    // Convert Entity → DTO
    // ==========================
    public static UserDto fromEntity(User user) {
        return Optional.ofNullable(user)
                .map(u -> new UserDto(
                        u.getId(),
                        u.getName(),
                        u.getEmail(),
                        u.getPassword(),
                        u.getRole().getName().name()))
                .orElse(null);
    }

    // ==========================
    // Convert DTO → Entity
    // ==========================
    public User toEntity(Role roleEntity) {
        User user = new User(name, email, password, roleEntity);
        user.setId(id);
        return user;
    }
}