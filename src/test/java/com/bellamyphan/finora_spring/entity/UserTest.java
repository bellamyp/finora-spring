package com.bellamyphan.finora_spring.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserTest {

    @Test
    void testConstructorAndGettersSetters() {
        Role role = new Role("USER");

        // Using parameterized constructor
        User user = new User("John", "Doe", "johndoe", "password123", role);

        // Check getters
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("johndoe", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals(role, user.getRole());

        // Check setters
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setUsername("janesmith");
        user.setPassword("newpass");
        Role newRole = new Role("ADMIN");
        user.setRole(newRole);

        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("janesmith", user.getUsername());
        assertEquals("newpass", user.getPassword());
        assertEquals(newRole, user.getRole());

        // id should be null initially
        assertNull(user.getId());
    }

    @Test
    void testEqualsAndHashCode() {
        Role role = new Role("USER");
        User user1 = new User("John", "Doe", "johndoe", "pass", role);
        User user2 = new User("John", "Doe", "johndoe", "pass", role);

        // Lombok @Data: equals/hashCode uses all fields except id (if null)
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testNotEmptyFieldsManually() {
        Role role = new Role("USER");

        User user = new User("John", "Doe", "johndoe", "password123", role);

        // Simple manual checks instead of Validator
        assertNotNull(user.getUsername());
        assertFalse(user.getUsername().isBlank());

        assertNotNull(user.getPassword());
        assertFalse(user.getPassword().isBlank());
    }
}
