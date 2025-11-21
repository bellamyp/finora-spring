package com.bellamyphan.finora_spring.constant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class RoleEnumMockitoTest {

    @Test
    void testFromRoleNameWithMockito() {
        try (MockedStatic<RoleEnum> mocked = mockStatic(RoleEnum.class)) {
            mocked.when(() -> RoleEnum.fromRoleName("ROLE_ADMIN"))
                    .thenReturn(RoleEnum.ROLE_ADMIN);

            RoleEnum result = RoleEnum.fromRoleName("ROLE_ADMIN");
            assertEquals(RoleEnum.ROLE_ADMIN, result);

            mocked.verify(() -> RoleEnum.fromRoleName("ROLE_ADMIN"));
        }
    }

    @Test
    void testFromDisplayNameWithMockito() {
        try (MockedStatic<RoleEnum> mocked = mockStatic(RoleEnum.class)) {
            mocked.when(() -> RoleEnum.fromDisplayName("User"))
                    .thenReturn(RoleEnum.ROLE_USER);

            RoleEnum result = RoleEnum.fromDisplayName("User");
            assertEquals(RoleEnum.ROLE_USER, result);

            mocked.verify(() -> RoleEnum.fromDisplayName("User"));
        }
    }
}