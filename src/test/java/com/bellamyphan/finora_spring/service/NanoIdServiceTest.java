package com.bellamyphan.finora_spring.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class NanoIdServiceTest {

    private NanoIdService nanoIdService;
    private final int defaultLength = Integer.parseInt(
            System.getProperty("nanoid.default.length", "10") // fallback to 10 if property not set
    );

    @BeforeEach
    void setUp() {
        // Inject default length from system property (simulating application.properties)
        nanoIdService = new NanoIdService(defaultLength);
    }

    // ----------------------------
    // 1️⃣ Deterministic test using static mock
    // ----------------------------
    @Test
    void testGenerateWithMockedNanoId() {
        try (MockedStatic<NanoIdUtils> utilities = mockStatic(NanoIdUtils.class)) {
            utilities.when(() -> NanoIdUtils.randomNanoId(any(), any(), anyInt()))
                    .thenReturn("ABCDEFGHIJ");

            String id = nanoIdService.generate();

            assertEquals("ABCDEFGHIJ", id, "NanoID should match mocked value");
            assertEquals(defaultLength, id.length(), "NanoID should have length from properties");
        }
    }

    // ----------------------------
    // 2️⃣ Real NanoID generation test
    // ----------------------------
    @Test
    void testGenerateRealNanoIdLength() {
        String id = nanoIdService.generate();

        assertNotNull(id, "Generated NanoID should not be null");
        assertEquals(defaultLength, id.length(), "Generated NanoID should have length from properties");
    }

    // ----------------------------
    // 3️⃣ Sanity check for uniqueness
    // ----------------------------
    @Test
    void testGenerateMultipleUniqueNanoIds() {
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String id = nanoIdService.generate();
            assertFalse(ids.contains(id), "Duplicate NanoID detected");
            ids.add(id);
        }
    }

    // ----------------------------
    // 4️⃣ Optional: Verify constructor injection
    // ----------------------------
    @Test
    void testConstructorInjectedDefaultLength() {
        int customLength = 12;
        NanoIdService serviceWithCustomLength = new NanoIdService(customLength);
        String id = serviceWithCustomLength.generate();

        assertEquals(customLength, id.length(), "NanoID should match the injected length");
    }
}