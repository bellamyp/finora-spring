package com.bellamyphan.finora_spring.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BrandTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Brand brand = new Brand();

        // Create a dummy user
        User user = new User();
        user.setId("user123");

        // Set values
        brand.setId("brand12345");
        brand.setUser(user);
        brand.setName("Nike");
        brand.setLocation("USA");

        // Assertions
        assertEquals("brand12345", brand.getId());
        assertEquals(user, brand.getUser());
        assertEquals("Nike", brand.getName());
        assertEquals("USA", brand.getLocation());
    }

    @Test
    void testConstructorWithUser() {
        User user = new User();
        user.setId("user456");

        Brand brand = new Brand(user, "Puma", "Germany");

        // Assertions
        assertNull(brand.getId(), "ID should be null if not provided");
        assertEquals(user, brand.getUser());
        assertEquals("Puma", brand.getName());
        assertEquals("Germany", brand.getLocation());
    }

    @Test
    void testSettersAndGetters() {
        Brand brand = new Brand();

        User user = new User();
        user.setId("user789");

        brand.setId("brand99999");
        brand.setUser(user);
        brand.setName("Reebok");
        brand.setLocation("UK");

        assertEquals("brand99999", brand.getId());
        assertEquals(user, brand.getUser());
        assertEquals("Reebok", brand.getName());
        assertEquals("UK", brand.getLocation());
    }
}
