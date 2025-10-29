package com.bellamyphan.finora_spring.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import com.bellamyphan.finora_spring.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoleTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceMock roleService; // a simple wrapper to call repository

    @Test
    void testRoleConstructorAndGettersSetters() {
        // Arrange
        Role role = new Role("ADMIN");

        // Act & Assert
        assertEquals("ADMIN", role.getName());

        role.setName("USER");
        assertEquals("USER", role.getName());

        // id is null before saving
        assertNull(role.getId());
    }

    @Test
    void testSaveAndFindAllRoles() {
        // Arrange
        Role role1 = new Role("ADMIN");
        Role role2 = new Role("USER");

        when(roleRepository.findAll()).thenReturn(Arrays.asList(role1, role2));

        // Act
        List<Role> roles = roleService.getAllRoles();

        // Assert
        assertEquals(2, roles.size());
        assertEquals("ADMIN", roles.get(0).getName());
        assertEquals("USER", roles.get(1).getName());

        verify(roleRepository, times(1)).findAll();
    }

    // Simple wrapper class to inject the mock repository
    record RoleServiceMock(RoleRepository roleRepository) {

        List<Role> getAllRoles() {
            return roleRepository.findAll();
        }
    }
}
