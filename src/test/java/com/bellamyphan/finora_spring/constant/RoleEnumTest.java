package com.bellamyphan.finora_spring.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleEnumTest {

    // ----------------------------
    // 1️⃣ Test valid role parsing
    // ----------------------------
    @Test
    void testFromRoleNameValid() {
        assertEquals(RoleEnum.ROLE_ADMIN, RoleEnum.fromRoleName("ROLE_ADMIN"));
        assertEquals(RoleEnum.ROLE_ADMIN, RoleEnum.fromRoleName("role_admin"));  // case-insensitive
        assertEquals(RoleEnum.ROLE_USER, RoleEnum.fromRoleName("ROLE_USER"));
        assertEquals(RoleEnum.ROLE_USER, RoleEnum.fromRoleName("role_user"));
    }

    // ----------------------------
    // 2️⃣ Test invalid input throws exception
    // ----------------------------
    @Test
    void testFromRoleNameInvalid() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> RoleEnum.fromRoleName("manager"));

        assertTrue(ex.getMessage().contains("No matching role"));
    }

    // ----------------------------
    // 3️⃣ Validate enum values order & count
    // ----------------------------
    @Test
    void testEnumValues() {
        RoleEnum[] values = RoleEnum.values();

        assertEquals(2, values.length);
        assertSame(RoleEnum.ROLE_ADMIN, values[0]);
        assertSame(RoleEnum.ROLE_USER, values[1]);
    }
}
