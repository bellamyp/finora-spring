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

        // Set values
        brand.setId("brand12345");
        brand.setName("Nike");
        brand.setLocation("USA");

        // Assertions
        assertEquals("brand12345", brand.getId());
        assertEquals("Nike", brand.getName());
        assertEquals("USA", brand.getLocation());
    }

    @Test
    void testConstructorWithoutId() {
        Brand brand = new Brand("Puma", "Germany");

        // Assertions
        assertNull(brand.getId(), "ID should be null if not provided");
        assertEquals("Puma", brand.getName());
        assertEquals("Germany", brand.getLocation());
    }

    @Test
    void testSettersAndGetters() {
        Brand brand = new Brand();
        brand.setId("brand99999");
        brand.setName("Reebok");
        brand.setLocation("UK");

        assertEquals("brand99999", brand.getId());
        assertEquals("Reebok", brand.getName());
        assertEquals("UK", brand.getLocation());
    }
}