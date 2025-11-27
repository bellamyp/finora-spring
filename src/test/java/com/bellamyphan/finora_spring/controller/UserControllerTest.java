package com.bellamyphan.finora_spring.controller;

import com.bellamyphan.finora_spring.constant.RoleEnum;
import com.bellamyphan.finora_spring.dto.UserDto;
import com.bellamyphan.finora_spring.dto.UserRequestDto;
import com.bellamyphan.finora_spring.entity.Role;
import com.bellamyphan.finora_spring.entity.User;
import com.bellamyphan.finora_spring.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    private User user;
    private UserDto userDto;
    private UserRequestDto requestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user123");
        user.setEmail("test@example.com");
        user.setName("Test User");

        // FIX: set a role
        Role role = new Role();
        role.setName(RoleEnum.ROLE_USER);
        user.setRole(role);

        userDto = UserDto.fromEntity(user);

        requestDto = new UserRequestDto("Test User", "TEST@EXAMPLE.com", "password", "ROLE_USER");
    }

    @Test
    void getAllUsers_ReturnsUserDtos() {
        when(userService.findAll()).thenReturn(List.of(user));

        List<UserDto> result = controller.getAllUsers();

        assertThat(result).containsExactly(userDto);
        verify(userService, times(1)).findAll();
    }

    @Test
    void createUser_ReturnsCreatedUser() {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userService.createUser(any(UserRequestDto.class))).thenReturn(user);

        ResponseEntity<?> response = controller.createUser(requestDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(userDto);

        verify(userService, times(1)).findByEmail("test@example.com");
        verify(userService, times(1)).createUser(any(UserRequestDto.class));
    }

    @Test
    void createUser_ReturnsConflict_WhenEmailExists() {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = controller.createUser(requestDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isEqualTo("Email already exists");

        verify(userService, times(1)).findByEmail("test@example.com");
        verify(userService, never()).createUser(any());
    }

    @Test
    void createUser_ReturnsBadRequest_OnIllegalArgumentException() {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userService.createUser(any(UserRequestDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid input"));

        ResponseEntity<?> response = controller.createUser(requestDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Invalid input");
    }

    @Test
    void createUser_ReturnsServerError_OnRuntimeException() {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userService.createUser(any(UserRequestDto.class)))
                .thenThrow(new RuntimeException("DB down"));

        ResponseEntity<?> response = controller.createUser(requestDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo("Error creating user: DB down");
    }
}
