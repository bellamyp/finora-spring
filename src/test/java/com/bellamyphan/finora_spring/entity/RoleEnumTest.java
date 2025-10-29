package com.bellamyphan.finora_spring.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoleEnumTest {

    @Test
    void getRoleName_shouldReturnEnumName() {
        assertEquals("ROLE_ADMIN", RoleEnum.ROLE_ADMIN.getRoleName());
        assertEquals("ROLE_USER", RoleEnum.ROLE_USER.getRoleName());
        assertEquals("ROLE_DEMO", RoleEnum.ROLE_DEMO.getRoleName());
    }

    @Test
    void fromRoleName_valid_shouldReturnEnum() {
        assertEquals(RoleEnum.ROLE_ADMIN, RoleEnum.fromRoleName("ROLE_ADMIN"));
        assertEquals(RoleEnum.ROLE_USER, RoleEnum.fromRoleName("role_user")); // case-insensitive
        assertEquals(RoleEnum.ROLE_DEMO, RoleEnum.fromRoleName("Role_Demo"));
    }

    @Test
    void fromRoleName_invalid_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                RoleEnum.fromRoleName("ROLE_UNKNOWN"));
        assertTrue(exception.getMessage().contains("No matching role for name"));
    }

    @Test
    void fromDisplayName_valid_shouldReturnEnum() {
        assertEquals(RoleEnum.ROLE_ADMIN, RoleEnum.fromDisplayName("Admin"));
        assertEquals(RoleEnum.ROLE_USER, RoleEnum.fromDisplayName("user")); // case-insensitive
        assertEquals(RoleEnum.ROLE_DEMO, RoleEnum.fromDisplayName("DEMO"));
    }

    @Test
    void fromDisplayName_invalid_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                RoleEnum.fromDisplayName("Unknown"));
        assertTrue(exception.getMessage().contains("No matching role for display name"));
    }

    @Test
    void displayNameGetter_shouldReturnCorrectValue() {
        assertEquals("Admin", RoleEnum.ROLE_ADMIN.getDisplayName());
        assertEquals("User", RoleEnum.ROLE_USER.getDisplayName());
        assertEquals("Demo", RoleEnum.ROLE_DEMO.getDisplayName());
    }
}
