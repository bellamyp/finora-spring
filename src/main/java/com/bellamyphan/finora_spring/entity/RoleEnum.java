package com.bellamyphan.finora_spring.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {

    ROLE_ADMIN("Admin"),
    ROLE_USER("User");

    private final String displayName;

    public String getRoleName() {
        return this.name(); // "ROLE_ADMIN", "ROLE_USER"
    }

    public static RoleEnum fromRoleName(String roleName) {
        for (RoleEnum role : values()) {
            if (role.name().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No matching role for name: " + roleName);
    }

    public static RoleEnum fromDisplayName(String displayName) {
        for (RoleEnum role : values()) {
            if (role.displayName.equalsIgnoreCase(displayName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No matching role for display name: " + displayName);
    }
}
