package com.bellamyphan.finora_spring.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserTest {

    @Test
    void testConstructorAndGettersSetters() {
        Role role = new Role("USER");

        // Using parameterized constructor
        User user = new User("John Doe", "johndoe@email.com", "password123", role);

        // Check getters
        assertEquals("John Doe", user.getName());
        assertEquals("johndoe@email.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(role, user.getRole());

        // Check setters
        user.setName("Jane Smith");
        user.setEmail("janesmith@email.com");
        user.setPassword("newpass");
        Role newRole = new Role("ADMIN");
        user.setRole(newRole);

        assertEquals("Jane Smith", user.getName());
        assertEquals("janesmith@email.com", user.getEmail());
        assertEquals("newpass", user.getPassword());
        assertEquals(newRole, user.getRole());

        // id should be null initially
        assertNull(user.getId());
    }

    @Test
    void testEqualsAndHashCode() {
        Role role = new Role("USER");
        User user1 = new User("John Doe", "johndoe@email.com", "pass", role);
        User user2 = new User("John Doe", "johndoe@email.com", "pass", role);

        // Lombok @Data: equals/hashCode uses all fields (except id if null)
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testNotEmptyFieldsManually() {
        Role role = new Role("USER");
        User user = new User("John Doe", "johndoe@email.com", "password123", role);

        // Simple manual checks instead of Validator
        assertNotNull(user.getName());
        assertFalse(user.getName().isBlank());

        assertNotNull(user.getEmail());
        assertFalse(user.getEmail().isBlank());

        assertNotNull(user.getPassword());
        assertFalse(user.getPassword().isBlank());

        assertNotNull(user.getRole());
    }
}
