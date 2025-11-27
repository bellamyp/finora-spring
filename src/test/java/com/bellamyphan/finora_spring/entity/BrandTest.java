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

        brand.setId("brand12345");
        brand.setName("Nike");
        brand.setUrl("https://nike.com");

        assertEquals("brand12345", brand.getId());
        assertEquals("Nike", brand.getName());
        assertEquals("https://nike.com", brand.getUrl());
    }

    @Test
    void testConstructorWithNameAndUrl() {
        Brand brand = new Brand("Puma", "https://puma.com");

        assertNull(brand.getId(), "ID should be null when not provided");
        assertEquals("Puma", brand.getName());
        assertEquals("https://puma.com", brand.getUrl());
    }

    @Test
    void testSettersAndGetters() {
        Brand brand = new Brand();

        brand.setId("brand99999");
        brand.setName("Reebok");
        brand.setUrl("https://reebok.com");

        assertEquals("brand99999", brand.getId());
        assertEquals("Reebok", brand.getName());
        assertEquals("https://reebok.com", brand.getUrl());
    }
}
