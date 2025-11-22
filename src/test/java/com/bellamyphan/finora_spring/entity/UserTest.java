package com.bellamyphan.finora_spring.entity;

import static org.junit.jupiter.api.Assertions.*;

import com.bellamyphan.finora_spring.constant.RoleEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserTest {

    @Test
    void testConstructorAndGettersSetters() {
        // Arrange
        Role role = new Role(RoleEnum.ROLE_USER);

        // Using constructor without ID
        User user = new User("John Doe", "johndoe@email.com", "password123", role);

        // Check getters
        assertEquals("John Doe", user.getName());
        assertEquals("johndoe@email.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(role, user.getRole());
        assertNull(user.getId(), "ID should be null initially");

        // Modify using setters
        user.setName("Jane Smith");
        user.setEmail("janesmith@email.com");
        user.setPassword("newpass");
        Role newRole = new Role(RoleEnum.ROLE_ADMIN);
        user.setRole(newRole);

        // Check updated values
        assertEquals("Jane Smith", user.getName());
        assertEquals("janesmith@email.com", user.getEmail());
        assertEquals("newpass", user.getPassword());
        assertEquals(newRole, user.getRole());
    }

    @Test
    void testEqualsAndHashCode() {
        Role role = new Role(RoleEnum.ROLE_USER);
        User user1 = new User("John Doe", "johndoe@email.com", "pass", role);
        User user2 = new User("John Doe", "johndoe@email.com", "pass", role);

        // Lombok @Data: equals/hashCode uses all fields (except null ID)
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testNotEmptyFieldsManually() {
        Role role = new Role(RoleEnum.ROLE_USER);
        User user = new User("John Doe", "johndoe@email.com", "password123", role);

        assertNotNull(user.getName());
        assertFalse(user.getName().isBlank());

        assertNotNull(user.getEmail());
        assertFalse(user.getEmail().isBlank());

        assertNotNull(user.getPassword());
        assertFalse(user.getPassword().isBlank());

        assertNotNull(user.getRole());
    }
}