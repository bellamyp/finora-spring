package com.bellamyphan.finora_spring.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {

    ROLE_ADMIN,
    ROLE_USER;

    // Optional: find enum from role name string (case-insensitive)
    public static RoleEnum fromRoleName(String roleName) {
        for (RoleEnum role : values()) {
            if (role.name().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No matching role for name: " + roleName);
    }
}
