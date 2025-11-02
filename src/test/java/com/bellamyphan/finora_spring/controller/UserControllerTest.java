package com.bellamyphan.finora_spring.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        // Arrange
        Role role = new Role();
        role.setId(1L);
        role.setName("USER");

        User user1 = new User("Alice", "alice@example.com", "password1", role);
        user1.setId(1L);

        User user2 = new User("Bob", "bob@example.com", "password2", role);
        user2.setId(2L);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Act
        List<User> actualUsers = userController.getAllUsers();

        // Assert
        assertEquals(2, actualUsers.size());
        assertEquals("Alice", actualUsers.get(0).getName());
        assertEquals("Bob", actualUsers.get(1).getName());
        verify(userRepository, times(1)).findAll();
    }
}
