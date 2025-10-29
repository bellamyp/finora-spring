package com.bellamyphan.finora_spring.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoleControllerTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleController roleController;

    @Test
    void getAllRoles_shouldReturnListOfRoles() {
        // Arrange
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ADMIN");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("USER");

        when(roleRepository.findAll()).thenReturn(Arrays.asList(role1, role2));

        // Act
        List<Role> actualRoles = roleController.getAllRoles();

        // Assert
        assertEquals(2, actualRoles.size());
        assertEquals("ADMIN", actualRoles.get(0).getName());
        assertEquals("USER", actualRoles.get(1).getName());
        verify(roleRepository, times(1)).findAll();
    }

}
