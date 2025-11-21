//package com.bellamyphan.finora_spring.controller;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import com.bellamyphan.finora_spring.dto.UserDto;
//import com.bellamyphan.finora_spring.entity.Role;
//import com.bellamyphan.finora_spring.enum2.RoleEnum;
//import com.bellamyphan.finora_spring.entity.User;
//import com.bellamyphan.finora_spring.repository.RoleRepository;
//import com.bellamyphan.finora_spring.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//@ExtendWith(MockitoExtension.class)
//public class UserControllerTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private RoleRepository roleRepository;
//
//    @InjectMocks
//    private UserController userController;
//
//    @Test
//    void getAllUsers_shouldReturnListOfUserDTOs() {
//        // Arrange
//        Role role = new Role();
//        role.setId(UUID.randomUUID());
//        role.setName("ROLE_USER");
//
//        User user1 = new User("Alice", "alice@example.com", "password1", role);
//        user1.setId(UUID.randomUUID());
//
//        User user2 = new User("Bob", "bob@example.com", "password2", role);
//        user2.setId(UUID.randomUUID());
//
//        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
//
//        // Act
//        List<UserDto> actualUsers = userController.getAllUsers();
//
//        // Assert
//        assertEquals(2, actualUsers.size());
//
//        UserDto dto1 = actualUsers.get(0);
//        UserDto dto2 = actualUsers.get(1);
//
//        assertEquals("Alice", dto1.getName());
//        assertEquals("ROLE_USER", dto1.getRole());
//
//        assertEquals("Bob", dto2.getName());
//        assertEquals("ROLE_USER", dto2.getRole());
//
//        verify(userRepository, times(1)).findAll();
//    }
//
//    @Test
//    void createUser_emailAlreadyExists_shouldReturnConflict() {
//        User user = new User("Alice", "alice@example.com", "password", null);
//        user.setId(UUID.randomUUID());
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
//
//        ResponseEntity<String> response = userController.createUser(user);
//
//        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
//        assertEquals("Email already exists", response.getBody());
//        verify(userRepository, never()).save(any());
//    }
//
//    @Test
//    void createUser_noRole_shouldAssignDefaultRole() {
//        User user = new User("Bob", "bob@example.com", "password", null);
//        user.setId(UUID.randomUUID());
//
//        Role defaultRole = new Role();
//        defaultRole.setId(UUID.randomUUID());
//        defaultRole.setName(RoleEnum.ROLE_USER.toString());
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
//        when(roleRepository.findByName(RoleEnum.ROLE_USER.toString())).thenReturn(Optional.of(defaultRole));
//
//        ResponseEntity<String> response = userController.createUser(user);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals("User created successfully", response.getBody());
//        assertEquals(defaultRole, user.getRole());
//        verify(userRepository, times(1)).save(user);
//    }
//
//    @Test
//    void createUser_roleIdInvalid_shouldReturnBadRequest() {
//        Role invalidRole = new Role();
//        invalidRole.setId(UUID.randomUUID());
//
//        User user = new User("Charlie", "charlie@example.com", "password", invalidRole);
//        user.setId(UUID.randomUUID());
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
//        when(roleRepository.findById(invalidRole.getId())).thenReturn(Optional.empty());
//
//        ResponseEntity<String> response = userController.createUser(user);
//
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals("Invalid role ID", response.getBody());
//        verify(userRepository, never()).save(any());
//    }
//
//    @Test
//    void createUser_defaultRoleMissing_shouldReturnInternalServerError() {
//        User user = new User("David", "david@example.com", "password", null);
//        user.setId(UUID.randomUUID());
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
//        when(roleRepository.findByName(RoleEnum.ROLE_USER.toString())).thenReturn(Optional.empty());
//
//        ResponseEntity<String> response = userController.createUser(user);
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//        assertEquals("ROLE_USER role not found in DB", response.getBody());
//        verify(userRepository, never()).save(any());
//    }
//
//    @Test
//    void createUser_roleProvidedExists_shouldSaveUserWithRole() {
//        Role adminRole = new Role();
//        adminRole.setId(UUID.randomUUID());
//        adminRole.setName("ROLE_ADMIN");
//
//        User user = new User("Eve", "eve@example.com", "password", adminRole);
//        user.setId(UUID.randomUUID());
//
//        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
//        when(roleRepository.findById(adminRole.getId())).thenReturn(Optional.of(adminRole));
//
//        ResponseEntity<String> response = userController.createUser(user);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals("User created successfully", response.getBody());
//        assertEquals(adminRole, user.getRole());
//        verify(userRepository, times(1)).save(user);
//    }
//}
