package com.bellamyphan.finora_spring.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import com.bellamyphan.finora_spring.constant.RoleEnum;
import com.bellamyphan.finora_spring.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoleTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceMock roleService; // Simple wrapper to call repository

    @Test
    void testRoleConstructorAndGettersSetters() {
        // Arrange
        Role role = new Role(RoleEnum.ROLE_ADMIN);

        // Act & Assert
        assertEquals(RoleEnum.ROLE_ADMIN, role.getName());

        role.setName(RoleEnum.ROLE_USER);
        assertEquals(RoleEnum.ROLE_USER, role.getName());

        // id is null before saving
        assertNull(role.getId());
    }

    @Test
    void testSaveAndFindAllRoles() {
        // Arrange
        Role role1 = new Role(RoleEnum.ROLE_ADMIN);
        Role role2 = new Role(RoleEnum.ROLE_USER);

        when(roleRepository.findAll()).thenReturn(Arrays.asList(role1, role2));

        // Act
        List<Role> roles = roleService.getAllRoles();

        // Assert
        assertEquals(2, roles.size());
        assertEquals(RoleEnum.ROLE_ADMIN, roles.get(0).getName());
        assertEquals(RoleEnum.ROLE_USER, roles.get(1).getName());

        verify(roleRepository, times(1)).findAll();
    }

    // Simple wrapper class to inject the mock repository
    record RoleServiceMock(RoleRepository roleRepository) {

        List<Role> getAllRoles() {
            return roleRepository.findAll();
        }
    }
}